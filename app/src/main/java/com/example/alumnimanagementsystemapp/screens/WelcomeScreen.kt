package com.example.alumnimanagementsystemapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavController) {
    var showContent by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Welcome to",
                        fontSize = 24.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "IST Alumni Management",
                        fontSize = 32.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Connect, Collaborate, and Grow",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Your gateway to a thriving IST alumni community",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        showContent = true
    }
}
