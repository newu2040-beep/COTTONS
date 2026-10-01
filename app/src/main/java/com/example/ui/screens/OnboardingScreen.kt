package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CottonsBackground
import com.example.ui.components.PaperCard
import com.example.ui.components.WashiTape
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.CottonsColors
import com.example.ui.theme.CottonsThemes
import com.example.ui.theme.HandFont
import com.example.ui.theme.LocalCottons
import com.example.ui.theme.PaperStyle
import com.example.ui.theme.SansFont
import com.example.ui.theme.SerifFont

@Composable
fun OnboardingScreen(
    currentTheme: CottonsColors,
    onFinish: (name: String, themeName: String) -> Unit
) {
    var page by remember { mutableIntStateOf(0) }
    var userName by remember { mutableStateOf("Rahul") }
    var selectedTheme by remember { mutableStateOf(currentTheme.name) }

    CottonsBackground(colors = currentTheme, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Skip button & Wax Seal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WaxSealBadge(size = 52.dp)
                Text(
                    text = "Skip",
                    fontFamily = HandFont,
                    fontSize = 18.sp,
                    color = currentTheme.inkSoft,
                    modifier = Modifier.clickable { onFinish(userName, selectedTheme) }
                )
            }

            // Center card content based on page
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (page) {
                    0 -> OnboardingPageOne()
                    1 -> OnboardingPageTwo()
                    else -> OnboardingPageThree(
                        name = userName,
                        onNameChange = { userName = it },
                        selectedTheme = selectedTheme,
                        onThemeChange = { selectedTheme = it }
                    )
                }
            }

            // Bottom Navigation dots & CTA Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (page == index) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (page == index) currentTheme.primary
                                    else currentTheme.inkSoft.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // CTA Button: "Get Started ->" or "Next ->"
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(28.dp), ambientColor = currentTheme.shadow)
                        .clip(RoundedCornerShape(28.dp))
                        .background(currentTheme.primary)
                        .clickable {
                            if (page < 2) {
                                page++
                            } else {
                                onFinish(userName, selectedTheme)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (page == 2) "Get Started  →" else "Continue  →",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun OnboardingPageOne() {
    val cottons = LocalCottons.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PaperCard(
            modifier = Modifier.fillMaxWidth(0.92f),
            style = PaperStyle.LINED_CREAM,
            tape = true,
            tapeRotation = -3f,
            rotation = -1.5f,
            hasHoles = true
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "COTTONS",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    color = cottons.ink,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "plan • focus • create\na cozier you",
                    fontFamily = HandFont,
                    fontSize = 20.sp,
                    color = cottons.inkSoft,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Cute cat / cherries sticker cluster
        Box(contentAlignment = Alignment.Center) {
            PaperCard(
                modifier = Modifier.fillMaxWidth(0.85f),
                style = PaperStyle.KRAFT,
                rotation = 2f
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    com.example.ui.components.CozyStickerImage(
                        drawableRes = R.drawable.sticker_cat,
                        fallbackEmoji = "🐱",
                        contentDescription = "Cozy Cat",
                        modifier = Modifier.size(68.dp),
                        emojiSize = 42.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Turn your plans, ideas, and daily moments into something beautiful.",
                        fontFamily = HandFont,
                        fontSize = 17.sp,
                        color = cottons.ink,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageTwo() {
    val cottons = LocalCottons.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PaperCard(
            modifier = Modifier.fillMaxWidth(0.92f),
            style = PaperStyle.LINED_BLUE,
            tape = true,
            tapeColor = cottons.tertiary,
            rotation = 1f
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cassette Focus 📼",
                    fontFamily = SerifFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = cottons.ink
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Wind your tapes, put on soothing café or rain sounds, and immerse yourself in gentle deep work sessions.",
                    fontFamily = SansFont,
                    fontSize = 15.sp,
                    color = cottons.inkSoft,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        PaperCard(
            modifier = Modifier.fillMaxWidth(0.88f),
            style = PaperStyle.LINED_PINK,
            rotation = -2f
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "💮", fontSize = 38.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Earn Wax Seals & Stamps",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = cottons.ink
                    )
                    Text(
                        text = "Complete your daily goals to seal your day in wax.",
                        fontFamily = HandFont,
                        fontSize = 15.sp,
                        color = cottons.inkSoft
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageThree(
    name: String,
    onNameChange: (String) -> Unit,
    selectedTheme: String,
    onThemeChange: (String) -> Unit
) {
    val cottons = LocalCottons.current

    PaperCard(
        modifier = Modifier.fillMaxWidth(0.95f),
        style = PaperStyle.LINED_CREAM,
        tape = true,
        rotation = 0f
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Make It Yours ♥",
                fontFamily = SerifFont,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = cottons.ink
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "What should we call you in your journal?",
                fontFamily = HandFont,
                fontSize = 17.sp,
                color = cottons.inkSoft
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                placeholder = { Text("Your Name", fontFamily = HandFont) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cottons.primary,
                    unfocusedBorderColor = cottons.inkSoft.copy(alpha = 0.4f),
                    focusedTextColor = cottons.ink,
                    unfocusedTextColor = cottons.ink
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Choose your starter theme:",
                fontFamily = SansFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = cottons.ink
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CottonsThemes.all.take(4).forEach { themeItem ->
                    val isChosen = themeItem.name == selectedTheme
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(if (isChosen) 4.dp else 1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(themeItem.primary)
                            .border(
                                width = if (isChosen) 3.dp else 1.5.dp,
                                color = if (isChosen) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onThemeChange(themeItem.name) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChosen) {
                            Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
