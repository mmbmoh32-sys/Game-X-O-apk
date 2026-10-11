package com.example.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.PlayerMark
import com.example.model.WinningLine
import com.example.model.WinningLineType
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCellBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import kotlin.random.Random

@Composable
fun NeonBoard(
    board: List<PlayerMark>,
    winningLine: WinningLine?,
    isGameOver: Boolean,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for winning highlight
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = NeonCyan.copy(alpha = 0.25f),
                spotColor = NeonPink.copy(alpha = 0.25f)
            )
            .background(DarkSurface, RoundedCornerShape(24.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.5f), DarkBorder, NeonPink.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(12.dp)
    ) {
        // 3x3 Grid of interactive cells
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val mark = board[index]
                        val isWinningCell = winningLine != null &&
                                (index == winningLine.a || index == winningLine.b || index == winningLine.c)

                        NeonCell(
                            mark = mark,
                            isWinningCell = isWinningCell,
                            pulseAlpha = pulseAlpha,
                            enabled = !isGameOver && mark == PlayerMark.NONE,
                            onClick = { onCellClick(index) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .testTag("cell_$index")
                        )
                    }
                }
            }
        }

        // Animated neon winning strike line
        if (winningLine != null) {
            WinningLineOverlay(
                winningLine = winningLine,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun NeonCell(
    mark: PlayerMark,
    isWinningCell: Boolean,
    pulseAlpha: Float,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Cell entry scale animation
    val scale = remember { Animatable(0f) }
    LaunchedEffect(mark) {
        if (mark != PlayerMark.NONE) {
            scale.snapTo(0.2f)
            scale.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
        }
    }

    val cellBorderColor = when {
        isWinningCell -> NeonGold.copy(alpha = pulseAlpha)
        mark == PlayerMark.X -> NeonCyan.copy(alpha = 0.35f)
        mark == PlayerMark.O -> NeonPink.copy(alpha = 0.35f)
        else -> DarkBorder.copy(alpha = 0.7f)
    }

    val cellBg = when {
        isWinningCell -> DarkCellBg.copy(alpha = 0.9f)
        else -> DarkCellBg
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cellBg)
            .border(
                width = if (isWinningCell) 2.5.dp else 1.dp,
                color = cellBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = NeonCyan),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (mark != PlayerMark.NONE) {
            NeonMarkCanvas(
                mark = mark,
                scale = scale.value,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            )
        }
    }
}

@Composable
fun NeonMarkCanvas(
    mark: PlayerMark,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.16f

        if (mark == PlayerMark.X) {
            val pad = w * 0.15f * (1f - scale * 0.5f)
            val start1 = Offset(pad, pad)
            val end1 = Offset(w - pad, h - pad)
            val start2 = Offset(w - pad, pad)
            val end2 = Offset(pad, h - pad)

            // Outer soft glow
            drawLine(
                color = NeonCyan.copy(alpha = 0.35f),
                start = start1,
                end = end1,
                strokeWidth = stroke * 2.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = NeonCyan.copy(alpha = 0.35f),
                start = start2,
                end = end2,
                strokeWidth = stroke * 2.2f,
                cap = StrokeCap.Round
            )

            // Inner intense glow
            drawLine(
                color = NeonCyan,
                start = start1,
                end = end1,
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = NeonCyan,
                start = start2,
                end = end2,
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // Core bright white laser
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = start1,
                end = end1,
                strokeWidth = stroke * 0.35f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = start2,
                end = end2,
                strokeWidth = stroke * 0.35f,
                cap = StrokeCap.Round
            )
        } else if (mark == PlayerMark.O) {
            val radius = (w / 2f - stroke / 2f) * scale
            val center = Offset(w / 2f, h / 2f)

            // Outer glow
            drawCircle(
                color = NeonPink.copy(alpha = 0.35f),
                center = center,
                radius = radius,
                style = Stroke(width = stroke * 2.2f)
            )

            // Inner intense neon
            drawCircle(
                color = NeonPink,
                center = center,
                radius = radius,
                style = Stroke(width = stroke)
            )

            // Core bright white laser
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                center = center,
                radius = radius,
                style = Stroke(width = stroke * 0.35f)
            )
        }
    }
}

@Composable
fun WinningLineOverlay(
    winningLine: WinningLine,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(winningLine) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(450, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val (start, end) = when (winningLine.type) {
            WinningLineType.ROW_0 -> Offset(w * 0.08f, h * 0.17f) to Offset(w * 0.92f, h * 0.17f)
            WinningLineType.ROW_1 -> Offset(w * 0.08f, h * 0.50f) to Offset(w * 0.92f, h * 0.50f)
            WinningLineType.ROW_2 -> Offset(w * 0.08f, h * 0.83f) to Offset(w * 0.92f, h * 0.83f)
            WinningLineType.COL_0 -> Offset(w * 0.17f, h * 0.08f) to Offset(w * 0.17f, h * 0.92f)
            WinningLineType.COL_1 -> Offset(w * 0.50f, h * 0.08f) to Offset(w * 0.50f, h * 0.92f)
            WinningLineType.COL_2 -> Offset(w * 0.83f, h * 0.08f) to Offset(w * 0.83f, h * 0.92f)
            WinningLineType.DIAG_MAIN -> Offset(w * 0.1f, h * 0.1f) to Offset(w * 0.9f, h * 0.9f)
            WinningLineType.DIAG_ANTI -> Offset(w * 0.9f, h * 0.1f) to Offset(w * 0.1f, h * 0.9f)
        }

        val currentEnd = Offset(
            x = start.x + (end.x - start.x) * progress.value,
            y = start.y + (end.y - start.y) * progress.value
        )

        // Outer bloom
        drawLine(
            color = NeonGold.copy(alpha = 0.5f),
            start = start,
            end = currentEnd,
            strokeWidth = 22f,
            cap = StrokeCap.Round
        )

        // Mid neon beam
        drawLine(
            color = NeonGold,
            start = start,
            end = currentEnd,
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )

        // Intense laser core
        drawLine(
            color = Color.White,
            start = start,
            end = currentEnd,
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }
}

// Particle confetti celebration for victory
data class ConfettiParticle(
    val x: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)

@Composable
fun VictoryConfettiOverlay(modifier: Modifier = Modifier) {
    val particles = remember {
        val colors = listOf(NeonCyan, NeonPink, NeonGold, Color.White, Color(0xFF00FF88))
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                initialY = Random.nextFloat() * -0.5f,
                speed = Random.nextFloat() * 0.6f + 0.4f,
                size = Random.nextFloat() * 12f + 6f,
                color = colors.random()
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(2800, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (p in particles) {
            val curY = (p.initialY + progress.value * p.speed * 1.5f) * h
            if (curY in 0f..h) {
                drawCircle(
                    color = p.color.copy(alpha = 1f - (curY / h) * 0.3f),
                    center = Offset(p.x * w, curY),
                    radius = p.size / 2f
                )
            }
        }
    }
}
