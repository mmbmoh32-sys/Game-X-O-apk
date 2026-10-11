package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.GameMode
import com.example.model.GameStatus
import com.example.model.PlayerMark
import com.example.ui.components.FloatingReactionsOverlay
import com.example.ui.components.InGameChatBubblesOverlay
import com.example.ui.components.NeonBoard
import com.example.ui.components.PlayerHeader
import com.example.ui.components.ReactionChatBar
import com.example.ui.components.VictoryConfettiOverlay
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCellBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun GameBoardScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val roomState by viewModel.roomState.collectAsState()
    val gameMode by viewModel.selectedMode.collectAsState()
    val language by viewModel.appLanguage.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val reactions by viewModel.reactions.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    val room = roomState ?: return

    val isGameOver = room.status == GameStatus.FINISHED
    val isHostWin = room.winnerMark == PlayerMark.X
    val isDraw = room.isDraw

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, CircleShape)
                        .testTag("game_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Leave Match",
                        tint = TextPrimary
                    )
                }

                // Match Badge
                Box(
                    modifier = Modifier
                        .background(DarkSurface, RoundedCornerShape(14.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val badgeText = when (gameMode) {
                        GameMode.ONLINE_ROOM -> if (language == AppLanguage.ARABIC) "غرفة #${room.roomCode}" else "ROOM #${room.roomCode}"
                        GameMode.VS_AI -> if (language == AppLanguage.ARABIC) "تحدي الذكاء الاصطناعي" else "AI BATTLE"
                        GameMode.LOCAL_2P -> if (language == AppLanguage.ARABIC) "لاعبان على نفس الجهاز" else "PASS & PLAY"
                    }
                    Text(
                        text = badgeText,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                // Sound Toggle
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, CircleShape)
                        .testTag("game_sound_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle SFX",
                        tint = if (isSoundEnabled) NeonCyan else TextMuted
                    )
                }
            }

            // Player HUD (Avatars, Turn Indicator, 15s Timer)
            PlayerHeader(
                player1 = room.hostPlayer,
                player2 = room.guestPlayer,
                currentTurn = room.currentTurn,
                remainingSeconds = room.remainingSeconds,
                isGameOver = isGameOver,
                language = language
            )

            // Neon 3x3 Board
            NeonBoard(
                board = room.board,
                winningLine = room.winningLine,
                isGameOver = isGameOver,
                onCellClick = { index ->
                    viewModel.onCellClicked(index)
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // In-game Reactions & Quick Chat bar
            ReactionChatBar(
                onSendReaction = { emoji ->
                    viewModel.sendReaction(emoji, PlayerMark.X)
                },
                onSendChat = { text ->
                    viewModel.sendChatMessage(text, PlayerMark.X)
                },
                language = language
            )
        }

        // Live Chat Bubbles Overlay
        InGameChatBubblesOverlay(chatMessages = chatMessages)

        // Floating Emoji Reactions Overlay
        FloatingReactionsOverlay(reactions = reactions)

        // Victory Confetti celebration if Host wins
        if (isGameOver && isHostWin) {
            VictoryConfettiOverlay()
        }

        // Game Over Dialog / Card Overlay
        AnimatedVisibility(
            visible = isGameOver,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            GameOverModal(
                winner = room.winnerMark,
                isDraw = isDraw,
                language = language,
                onRematch = { viewModel.requestRematch() },
                onBackToHome = { viewModel.navigateTo(AppScreen.HOME) }
            )
        }
    }
}

@Composable
fun GameOverModal(
    winner: PlayerMark,
    isDraw: Boolean,
    language: AppLanguage,
    onRematch: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when {
        isDraw -> AppStrings.drawTitle(language)
        winner == PlayerMark.X -> AppStrings.victoryTitle(language)
        else -> AppStrings.defeatTitle(language)
    }

    val subtitle = when {
        isDraw -> AppStrings.drawSubtitle(language)
        winner == PlayerMark.X -> AppStrings.victorySubtitle(language)
        else -> AppStrings.defeatSubtitle(language)
    }

    val titleColor = when {
        isDraw -> NeonGold
        winner == PlayerMark.X -> NeonCyan
        else -> NeonPink
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = titleColor.copy(alpha = 0.4f),
                spotColor = titleColor.copy(alpha = 0.4f)
            )
            .background(DarkSurfaceElevated, RoundedCornerShape(24.dp))
            .border(2.dp, titleColor, RoundedCornerShape(24.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Rematch Button
            Button(
                onClick = onRematch,
                colors = ButtonDefaults.buttonColors(containerColor = titleColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("rematch_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Rematch",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.playAgain(language),
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Exit to Lobby / Home
            TextButton(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exit_game_btn")
            ) {
                Text(
                    text = AppStrings.returnToMenu(language),
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
