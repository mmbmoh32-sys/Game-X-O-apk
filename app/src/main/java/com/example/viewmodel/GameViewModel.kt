package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.ProfileRepository
import com.example.engine.TicTacToeEngine
import com.example.model.AiDifficulty
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.ChatMessage
import com.example.model.GameMode
import com.example.model.GamePlayer
import com.example.model.GameStatus
import com.example.model.PlayerMark
import com.example.model.QUICK_CHATS
import com.example.model.Reaction
import com.example.model.RoomState
import com.example.model.UserProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
    HOME,
    LOBBY,
    GAME,
    PROFILE
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepo = ProfileRepository(application)
    val soundManager = SoundManager(application)

    val userProfile: StateFlow<UserProfile> = profileRepo.userProfile
    val appLanguage: StateFlow<AppLanguage> = profileRepo.appLanguage

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedMode = MutableStateFlow(GameMode.VS_AI)
    val selectedMode: StateFlow<GameMode> = _selectedMode.asStateFlow()

    private val _aiDifficulty = MutableStateFlow(AiDifficulty.UNBEATABLE)
    val aiDifficulty: StateFlow<AiDifficulty> = _aiDifficulty.asStateFlow()

    private val _roomState = MutableStateFlow<RoomState?>(null)
    val roomState: StateFlow<RoomState?> = _roomState.asStateFlow()

    private val _reactions = MutableStateFlow<List<Reaction>>(emptyList())
    val reactions: StateFlow<List<Reaction>> = _reactions.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(profileRepo.isSoundEnabled())
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isHapticsEnabled = MutableStateFlow(profileRepo.isHapticsEnabled())
    val isHapticsEnabled: StateFlow<Boolean> = _isHapticsEnabled.asStateFlow()

    private val _onlineOpponentPing = MutableStateFlow(42)
    val onlineOpponentPing: StateFlow<Int> = _onlineOpponentPing.asStateFlow()

    private var timerJob: Job? = null
    private var aiJob: Job? = null

    init {
        soundManager.isSoundEnabled = _isSoundEnabled.value
        soundManager.isHapticsEnabled = _isHapticsEnabled.value
    }

    fun navigateTo(screen: AppScreen) {
        soundManager.playButtonClick()
        _currentScreen.value = screen
    }

    fun toggleSound() {
        val next = !_isSoundEnabled.value
        _isSoundEnabled.value = next
        profileRepo.setSoundEnabled(next)
        soundManager.isSoundEnabled = next
    }

    fun toggleHaptics() {
        val next = !_isHapticsEnabled.value
        _isHapticsEnabled.value = next
        profileRepo.setHapticsEnabled(next)
        soundManager.isHapticsEnabled = next
        soundManager.playButtonClick()
    }

    fun setLanguage(lang: AppLanguage) {
        profileRepo.setLanguage(lang)
        soundManager.playButtonClick()
    }

    fun updatePlayerName(name: String) = profileRepo.updateName(name)
    fun updatePlayerAvatar(avatarId: String) = profileRepo.updateAvatar(avatarId)
    fun resetStats() = profileRepo.resetStats()

    fun setAiDifficulty(diff: AiDifficulty) {
        _aiDifficulty.value = diff
        soundManager.playButtonClick()
    }

    // --- GAME INITIALIZATION ---

    fun startVsAi(difficulty: AiDifficulty = _aiDifficulty.value) {
        _selectedMode.value = GameMode.VS_AI
        _aiDifficulty.value = difficulty
        val me = userProfile.value
        val host = GamePlayer(
            id = "player_1",
            name = me.name,
            mark = PlayerMark.X,
            avatarId = me.avatarId
        )
        val aiName = if (appLanguage.value == AppLanguage.ARABIC) {
            "الذكاء الاصطناعي (${AppStrings.getDifficultyLabel(difficulty, AppLanguage.ARABIC)})"
        } else {
            "Smart AI (${difficulty.label})"
        }
        val guest = GamePlayer(
            id = "ai_bot",
            name = aiName,
            mark = PlayerMark.O,
            avatarId = "glitch_bot"
        )
        initGameRoom(host, guest, "SOLO-AI")
    }

    fun startLocal2P(player2Name: String? = null) {
        _selectedMode.value = GameMode.LOCAL_2P
        val me = userProfile.value
        val p1Name = me.name.ifEmpty { if (appLanguage.value == AppLanguage.ARABIC) "اللاعب 1" else "Player 1" }
        val p2Name = player2Name ?: if (appLanguage.value == AppLanguage.ARABIC) "اللاعب 2" else "Player 2"
        val host = GamePlayer(
            id = "player_1",
            name = p1Name,
            mark = PlayerMark.X,
            avatarId = me.avatarId
        )
        val guest = GamePlayer(
            id = "player_2",
            name = p2Name,
            mark = PlayerMark.O,
            avatarId = "neon_king"
        )
        initGameRoom(host, guest, "LOCAL-2P")
    }

    fun createOnlineRoom(): String {
        _selectedMode.value = GameMode.ONLINE_ROOM
        val code = generateRoomCode()
        val me = userProfile.value
        val host = GamePlayer(
            id = "host_${System.currentTimeMillis()}",
            name = me.name,
            mark = PlayerMark.X,
            avatarId = me.avatarId
        )
        _roomState.value = RoomState(
            roomCode = code,
            hostPlayer = host,
            guestPlayer = null,
            status = GameStatus.WAITING_FOR_OPPONENT
        )
        _reactions.value = emptyList()
        _chatMessages.value = emptyList()
        _currentScreen.value = AppScreen.LOBBY
        soundManager.playButtonClick()
        return code
    }

    fun joinOnlineRoom(code: String) {
        _selectedMode.value = GameMode.ONLINE_ROOM
        val me = userProfile.value
        val hostName = if (appLanguage.value == AppLanguage.ARABIC) "مستضيف الغرفة" else "CyberHost"
        val host = GamePlayer(
            id = "host_remote",
            name = hostName,
            mark = PlayerMark.X,
            avatarId = "cosmic_star"
        )
        val guest = GamePlayer(
            id = "guest_${System.currentTimeMillis()}",
            name = me.name,
            mark = PlayerMark.O,
            avatarId = me.avatarId
        )
        initGameRoom(host, guest, code.uppercase())
    }

    fun simulateOpponentJoin() {
        val current = _roomState.value ?: return
        val rivalName = if (appLanguage.value == AppLanguage.ARABIC) "منافس النيون" else "NeonRival"
        val guest = GamePlayer(
            id = "guest_online",
            name = rivalName,
            mark = PlayerMark.O,
            avatarId = "neon_cat"
        )
        _roomState.value = current.copy(
            guestPlayer = guest,
            status = GameStatus.IN_PROGRESS,
            currentTurn = PlayerMark.X,
            remainingSeconds = 15
        )
        startTurnTimer()
        _currentScreen.value = AppScreen.GAME
        soundManager.playButtonClick()
    }

    private fun initGameRoom(host: GamePlayer, guest: GamePlayer, code: String) {
        _roomState.value = RoomState(
            roomCode = code,
            hostPlayer = host,
            guestPlayer = guest,
            currentTurn = PlayerMark.X,
            board = List(9) { PlayerMark.NONE },
            status = GameStatus.IN_PROGRESS,
            remainingSeconds = 15
        )
        _reactions.value = emptyList()
        _chatMessages.value = emptyList()
        startTurnTimer()
        _currentScreen.value = AppScreen.GAME
        soundManager.playButtonClick()
    }

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    // --- GAMEPLAY MOVES ---

    fun onCellClicked(index: Int) {
        val room = _roomState.value ?: return
        if (room.status != GameStatus.IN_PROGRESS) return
        if (room.board[index] != PlayerMark.NONE) return

        // In AI mode, human plays X
        if (_selectedMode.value == GameMode.VS_AI && room.currentTurn != PlayerMark.X) return

        makeMove(index, room.currentTurn)
    }

    private fun makeMove(index: Int, mark: PlayerMark) {
        val room = _roomState.value ?: return
        val newBoard = room.board.toMutableList().apply { set(index, mark) }

        if (mark == PlayerMark.X) {
            soundManager.playMoveX()
        } else {
            soundManager.playMoveO()
        }

        // Check for winning line
        val winLine = TicTacToeEngine.checkWinningLine(newBoard)
        if (winLine != null) {
            timerJob?.cancel()
            val winner = newBoard[winLine.a]
            _roomState.value = room.copy(
                board = newBoard,
                winningLine = winLine,
                winnerMark = winner,
                status = GameStatus.FINISHED
            )
            handleGameEnd(winner = winner, isDraw = false)
            return
        }

        // Check for draw
        if (TicTacToeEngine.isBoardFull(newBoard)) {
            timerJob?.cancel()
            _roomState.value = room.copy(
                board = newBoard,
                winningLine = null,
                winnerMark = PlayerMark.NONE,
                isDraw = true,
                status = GameStatus.FINISHED
            )
            handleGameEnd(winner = PlayerMark.NONE, isDraw = true)
            return
        }

        // Next turn
        val nextTurn = mark.opponent
        _roomState.value = room.copy(
            board = newBoard,
            currentTurn = nextTurn,
            remainingSeconds = 15
        )
        startTurnTimer()

        // Handle AI or Online Opponent response
        if (_selectedMode.value == GameMode.VS_AI && nextTurn == PlayerMark.O) {
            scheduleAiMove(newBoard)
        } else if (_selectedMode.value == GameMode.ONLINE_ROOM && nextTurn == PlayerMark.O) {
            scheduleSimulatedOnlineOpponentMove(newBoard)
        }
    }

    private fun scheduleAiMove(currentBoard: List<PlayerMark>) {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            // Realistic "thinking" delay
            delay(Random.nextLong(350, 750))
            if (!isActive) return@launch
            val room = _roomState.value ?: return@launch
            if (room.status != GameStatus.IN_PROGRESS || room.currentTurn != PlayerMark.O) return@launch

            val move = TicTacToeEngine.computeAiMove(currentBoard, PlayerMark.O, _aiDifficulty.value)
            if (move in 0..8) {
                makeMove(move, PlayerMark.O)
            }
        }
    }

    private fun scheduleSimulatedOnlineOpponentMove(currentBoard: List<PlayerMark>) {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            val thinkDelay = Random.nextLong(800, 1800)
            delay(thinkDelay)
            if (!isActive) return@launch
            val room = _roomState.value ?: return@launch
            if (room.status != GameStatus.IN_PROGRESS || room.currentTurn != PlayerMark.O) return@launch

            val move = TicTacToeEngine.computeAiMove(currentBoard, PlayerMark.O, AiDifficulty.MEDIUM)
            if (move in 0..8) {
                makeMove(move, PlayerMark.O)
            }

            // Occasionally send interactive reaction or quick chat from online opponent
            if (Random.nextFloat() < 0.35f) {
                delay(300)
                val chat = AppStrings.getQuickChats(appLanguage.value).random()
                sendChatMessage(chat, PlayerMark.O, room.guestPlayer?.name ?: "Opponent")
            }
        }
    }

    private fun handleGameEnd(winner: PlayerMark, isDraw: Boolean) {
        if (isDraw) {
            soundManager.playDraw()
            profileRepo.recordDraw()
        } else {
            val isHostWin = (winner == PlayerMark.X)
            if (isHostWin) {
                soundManager.playWin()
                profileRepo.recordWin()
            } else {
                soundManager.playLoss()
                profileRepo.recordLoss()
            }
        }
    }

    private fun startTurnTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val room = _roomState.value ?: break
                if (room.status != GameStatus.IN_PROGRESS) break

                val newSec = room.remainingSeconds - 1
                if (newSec <= 0) {
                    // Turn timer expired: auto-forfeit for current turn player
                    val winner = room.currentTurn.opponent
                    _roomState.value = room.copy(
                        remainingSeconds = 0,
                        status = GameStatus.FINISHED,
                        winnerMark = winner
                    )
                    handleGameEnd(winner, isDraw = false)
                    break
                } else {
                    _roomState.value = room.copy(remainingSeconds = newSec)
                }
            }
        }
    }

    // --- REMATCH & RESET ---

    fun requestRematch(asHost: Boolean = true) {
        val room = _roomState.value ?: return
        soundManager.playButtonClick()

        if (_selectedMode.value == GameMode.VS_AI || _selectedMode.value == GameMode.LOCAL_2P) {
            restartMatch()
            return
        }

        // Online mode: require rematch agreement
        val updated = if (asHost) room.copy(rematchHostRequested = true) else room.copy(rematchGuestRequested = true)
        _roomState.value = updated

        // Simulate opponent accepting rematch after brief pause
        viewModelScope.launch {
            delay(1200)
            restartMatch()
        }
    }

    fun restartMatch() {
        val room = _roomState.value ?: return
        _roomState.value = room.copy(
            board = List(9) { PlayerMark.NONE },
            currentTurn = PlayerMark.X,
            winningLine = null,
            winnerMark = PlayerMark.NONE,
            isDraw = false,
            remainingSeconds = 15,
            status = GameStatus.IN_PROGRESS,
            rematchHostRequested = false,
            rematchGuestRequested = false
        )
        startTurnTimer()
        soundManager.playButtonClick()
    }

    // --- SOCIAL & INTERACTIONS ---

    fun sendReaction(emoji: String, fromMark: PlayerMark = PlayerMark.X) {
        val room = _roomState.value ?: return
        val sender = if (fromMark == PlayerMark.X) room.hostPlayer.name else (room.guestPlayer?.name ?: "Guest")
        val reaction = Reaction(
            senderName = sender,
            emoji = emoji,
            fromPlayerMark = fromMark
        )
        _reactions.value = _reactions.value + reaction
        soundManager.playReaction()

        // Clean up reaction after 3.5 seconds
        viewModelScope.launch {
            delay(3500)
            _reactions.value = _reactions.value.filter { it.id != reaction.id }
        }
    }

    fun sendChatMessage(text: String, fromMark: PlayerMark = PlayerMark.X, senderName: String? = null) {
        val room = _roomState.value ?: return
        val name = senderName ?: if (fromMark == PlayerMark.X) room.hostPlayer.name else (room.guestPlayer?.name ?: "Guest")
        val msg = ChatMessage(
            senderName = name,
            text = text,
            fromPlayerMark = fromMark
        )
        _chatMessages.value = _chatMessages.value + msg
        soundManager.playReaction()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        aiJob?.cancel()
        soundManager.release()
    }
}
