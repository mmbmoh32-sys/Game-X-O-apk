package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.ChatMessage
import com.example.model.EMOJIS
import com.example.model.PlayerMark
import com.example.model.Reaction
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCellBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.random.Random

@Composable
fun ReactionChatBar(
    onSendReaction: (String) -> Unit,
    onSendChat: (String) -> Unit,
    language: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    var showQuickChat by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Quick Chat Drawer (expandable)
        AnimatedVisibility(
            visible = showQuickChat,
            enter = slideInVertically { it / 2 } + fadeIn(),
            exit = slideOutVertically { it / 2 } + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(DarkSurfaceElevated, RoundedCornerShape(16.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.quickChatTitle(language),
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { showQuickChat = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Chat",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (chat in AppStrings.getQuickChats(language)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(DarkCellBg)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                                    .clickable {
                                        onSendChat(chat)
                                        showQuickChat = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = chat,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom interactive Emoji Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(20.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chat toggle button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (showQuickChat) NeonCyan else DarkSurfaceElevated)
                    .clickable { showQuickChat = !showQuickChat }
                    .testTag("chat_toggle_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Quick Chat",
                    tint = if (showQuickChat) Color.Black else NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Emoji buttons row
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (emoji in EMOJIS) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onSendReaction(emoji) }
                            .testTag("emoji_$emoji"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingReactionsOverlay(
    reactions: List<Reaction>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        for (reaction in reactions) {
            FloatingEmojiItem(reaction = reaction)
        }
    }
}

@Composable
fun FloatingEmojiItem(reaction: Reaction) {
    val randomX = remember { Random.nextFloat() * 200f - 100f }
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(reaction.id) {
        offsetY.animateTo(
            targetValue = -350f,
            animationSpec = tween(2400, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(reaction.id) {
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(2400, delayMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    val isLeft = reaction.fromPlayerMark == PlayerMark.X
    val alignment = if (isLeft) Alignment.BottomStart else Alignment.BottomEnd

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 120.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = randomX.toInt(),
                        y = offsetY.value.toInt()
                    )
                }
                .background(
                    color = Color.Black.copy(alpha = alpha.value * 0.75f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = (if (isLeft) NeonCyan else NeonPink).copy(alpha = alpha.value),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = reaction.emoji,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reaction.senderName,
                    color = TextPrimary.copy(alpha = alpha.value),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InGameChatBubblesOverlay(
    chatMessages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    val recentMessage = chatMessages.lastOrNull() ?: return

    val alpha = remember(recentMessage.id) { Animatable(0f) }
    LaunchedEffect(recentMessage.id) {
        alpha.animateTo(1f, tween(200))
        kotlinx.coroutines.delay(2600)
        alpha.animateTo(0f, tween(300))
    }

    val isPlayerX = recentMessage.fromPlayerMark == PlayerMark.X
    val alignment = if (isPlayerX) Alignment.TopStart else Alignment.TopEnd
    val accent = if (isPlayerX) NeonCyan else NeonPink

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 90.dp),
        contentAlignment = alignment
    ) {
        if (alpha.value > 0.05f) {
            Box(
                modifier = Modifier
                    .background(DarkSurfaceElevated.copy(alpha = alpha.value), RoundedCornerShape(14.dp))
                    .border(1.5.dp, accent.copy(alpha = alpha.value), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = recentMessage.senderName,
                        color = accent.copy(alpha = alpha.value),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = recentMessage.text,
                        color = TextPrimary.copy(alpha = alpha.value),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
