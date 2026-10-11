package com.example.model

enum class PlayerMark {
    NONE, X, O;

    val opponent: PlayerMark
        get() = when (this) {
            X -> O
            O -> X
            NONE -> NONE
        }
}

enum class GameMode {
    VS_AI,
    LOCAL_2P,
    ONLINE_ROOM
}

enum class AiDifficulty(val label: String, val description: String) {
    EASY("Casual", "Casual bot that makes frequent mistakes"),
    MEDIUM("Tactical", "Smart bot that blocks your winning moves"),
    UNBEATABLE("Unbeatable (Minimax)", "Mathematically flawless AI. Can never be beaten!")
}

enum class GameStatus {
    WAITING_FOR_OPPONENT,
    IN_PROGRESS,
    FINISHED
}

data class WinningLine(
    val a: Int,
    val b: Int,
    val c: Int,
    val type: WinningLineType
)

enum class WinningLineType {
    ROW_0, ROW_1, ROW_2,
    COL_0, COL_1, COL_2,
    DIAG_MAIN, DIAG_ANTI
}

data class GamePlayer(
    val id: String,
    val name: String,
    val mark: PlayerMark,
    val avatarId: String,
    val isReady: Boolean = true,
    val isOnline: Boolean = true
)

data class Reaction(
    val id: Long = System.currentTimeMillis(),
    val senderName: String,
    val emoji: String,
    val fromPlayerMark: PlayerMark
)

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val senderName: String,
    val text: String,
    val fromPlayerMark: PlayerMark
)

data class RoomState(
    val roomCode: String,
    val hostPlayer: GamePlayer,
    val guestPlayer: GamePlayer?,
    val currentTurn: PlayerMark = PlayerMark.X,
    val board: List<PlayerMark> = List(9) { PlayerMark.NONE },
    val winningLine: WinningLine? = null,
    val winnerMark: PlayerMark = PlayerMark.NONE,
    val isDraw: Boolean = false,
    val remainingSeconds: Int = 15,
    val status: GameStatus = GameStatus.IN_PROGRESS,
    val rematchHostRequested: Boolean = false,
    val rematchGuestRequested: Boolean = false
)

data class UserProfile(
    val name: String = "NeonPlayer",
    val avatarId: String = "cyber_runner",
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
) {
    val totalGames: Int get() = wins + losses + draws
    val winRate: Int get() = if (totalGames == 0) 0 else ((wins.toFloat() / totalGames) * 100).toInt()
    val rankTitle: String
        get() = when {
            wins >= 50 -> "Neon Grandmaster 👑"
            wins >= 25 -> "Cyber Champion ⚡"
            wins >= 10 -> "Grid Veteran ⚔️"
            wins >= 3 -> "Neon Challenger 🎮"
            else -> "Rookie Contender 🔰"
        }
}

val AVATARS = listOf(
    "cyber_runner" to "⚡ Runner",
    "glitch_bot" to "🤖 Glitch",
    "neon_cat" to "🐱 Neon Cat",
    "neon_king" to "👑 Royalty",
    "retro_pixel" to "👾 8-Bit",
    "cosmic_star" to "🌌 Cosmic"
)

val QUICK_CHATS = listOf(
    "Good game! 👏",
    "Hurry up! ⏳",
    "Nice move! 🔥",
    "Oops! 😅",
    "Rematch? ⚔️",
    "GG! You're good! ⚡",
    "No way! 😱",
    "Checkmate! 👑"
)

val EMOJIS = listOf("🔥", "😂", "👏", "😱", "⚡", "👑", "💀", "💖")
