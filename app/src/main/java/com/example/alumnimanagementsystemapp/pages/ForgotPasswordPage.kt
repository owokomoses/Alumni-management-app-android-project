package com.example.alumnimanagementsystemapp.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthState
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.R

@Composable
fun ForgotPasswordPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }

    var emailIsFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Handle navigation based on the AuthState changes
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.ResetPasswordSent -> {
                Toast.makeText(context, "Reset password link sent. Check your email.", Toast.LENGTH_SHORT).show()
                // Navigate back to the login screen
                navController.navigate("login") {
                    popUpTo("forgot_password") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState.value as AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
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
            contentDescription = "Reset password Icon",
            modifier = Modifier.size(100.dp) // Adjust the size as necessary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Reset Password", fontSize = 32.sp, color = Color.Red)

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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email,imeAction = ImeAction.Done ),
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
            textStyle = TextStyle(color = Color.Gray,fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (email.isNotEmpty()) {
                authViewModel.resetPassword(email)
            } else {
                Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Reset password", color = Color.Black)
        }


    }
}
