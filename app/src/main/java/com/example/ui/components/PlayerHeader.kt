package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.GamePlayer
import com.example.model.PlayerMark
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlayerHeader(
    player1: GamePlayer,
    player2: GamePlayer?,
    currentTurn: PlayerMark,
    remainingSeconds: Int,
    isGameOver: Boolean,
    language: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player 1 (X)
        PlayerCard(
            player = player1,
            mark = PlayerMark.X,
            isCurrentTurn = currentTurn == PlayerMark.X && !isGameOver,
            accentColor = NeonCyan,
            language = language,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Center: Turn Timer & VS badge
        TurnTimerWidget(
            remainingSeconds = remainingSeconds,
            isGameOver = isGameOver,
            currentTurn = currentTurn,
            language = language,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Player 2 (O)
        PlayerCard(
            player = player2 ?: GamePlayer(
                id = "waiting",
                name = if (language == AppLanguage.ARABIC) "في الانتظار..." else "Waiting...",
                mark = PlayerMark.O,
                avatarId = "glitch_bot"
            ),
            mark = PlayerMark.O,
            isCurrentTurn = currentTurn == PlayerMark.O && !isGameOver,
            accentColor = NeonPink,
            language = language,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PlayerCard(
    player: GamePlayer,
    mark: PlayerMark,
    isCurrentTurn: Boolean,
    accentColor: Color,
    language: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isCurrentTurn) accentColor else DarkBorder,
        animationSpec = tween(300),
        label = "borderColor"
    )

    val shadowElevation = if (isCurrentTurn) 12.dp else 2.dp

    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isCurrentTurn) accentColor.copy(alpha = 0.4f) else Color.Transparent,
                spotColor = if (isCurrentTurn) accentColor.copy(alpha = 0.4f) else Color.Transparent
            )
            .background(DarkSurface, RoundedCornerShape(16.dp))
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar with mark badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .border(1.5.dp, accentColor.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarEmoji(player.avatarId),
                        fontSize = 22.sp
                    )
                }

                // Mark badge (X / O)
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mark.name,
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Player Name
            Text(
                text = player.name,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Turn status indicator
            Text(
                text = if (isCurrentTurn) AppStrings.yourTurn(language) else AppStrings.waitingTurn(language),
                color = if (isCurrentTurn) accentColor else TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun TurnTimerWidget(
    remainingSeconds: Int,
    isGameOver: Boolean,
    currentTurn: PlayerMark,
    language: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    val isUrgent = remainingSeconds <= 5 && !isGameOver
    val timerColor = when {
        isGameOver -> TextMuted
        isUrgent -> ErrorRed
        currentTurn == PlayerMark.X -> NeonCyan
        else -> NeonPink
    }

    val progress = if (isGameOver) 0f else (remainingSeconds / 15f).coerceIn(0f, 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTimer")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular timer canvas
            Canvas(modifier = Modifier.size(56.dp)) {
                val stroke = 4.dp.toPx()
                val radius = (size.width - stroke) / 2f
                val topLeft = Offset(stroke / 2f, stroke / 2f)
                val arcSize = Size(radius * 2f, radius * 2f)

                // Track
                drawArc(
                    color = DarkBorder,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )

                // Progress Arc
                if (!isGameOver) {
                    drawArc(
                        color = if (isUrgent) timerColor.copy(alpha = pulseAlpha) else timerColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
            }

            // Seconds text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isGameOver) "—" else "$remainingSeconds",
                    color = if (isUrgent) ErrorRed else TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = AppStrings.seconds(language),
                    color = TextMuted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // VS Tag
        Box(
            modifier = Modifier
                .background(DarkSurfaceElevated, RoundedCornerShape(6.dp))
                .border(0.5.dp, DarkBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "VS",
                color = NeonGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

fun avatarEmoji(avatarId: String): String {
    return when (avatarId) {
        "cyber_runner" -> "⚡"
        "glitch_bot" -> "🤖"
        "neon_cat" -> "🐱"
        "neon_king" -> "👑"
        "retro_pixel" -> "👾"
        "cosmic_star" -> "🌌"
        else -> "🎮"
    }
}
