package com.example.alumnimanagementsystemapp.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthState
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.R

@Composable
fun VerificationPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }

    var emailIsFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Observe the authState LiveData
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.VerificationEmailSent -> {
                Toast.makeText(
                    context,
                    "Verification email sent. Please check your email.",
                    Toast.LENGTH_SHORT
                ).show()
                // Optionally, navigate to another page or handle the state
                // For example, you might navigate to a login page:
                navController.navigate("login") {
                    popUpTo("signup") { inclusive = true }
                }
            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT)
                    .show()
            }

            else -> Unit
        }
    }



        Column(
            modifier = modifier.fillMaxSize().background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon), // Replace with your image resource
                contentDescription = "Login Icon",
                modifier = Modifier.size(100.dp) // Adjust the size as necessary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Verification", fontSize = 32.sp, color = Color.Red)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Email",
                        color = if (emailIsFocused) Color.Red else Color.Gray
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        emailIsFocused = focusState.isFocused
                    },
                textStyle = TextStyle(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authViewModel.sendVerificationEmail()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Verify account", color = Color.Black)
            }


        }
    }

