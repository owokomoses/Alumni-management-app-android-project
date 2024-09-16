package com.example.alumnimanagementsystemapp.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthState
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.R

@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var confirmPassword by remember { mutableStateOf("") }

    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var emailIsFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("verificationPage"){
                // Remove SignupPage from the back stack
                popUpTo("signup") { inclusive = true }
            }
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

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

        Text(text = "Signup", fontSize = 32.sp, color = Color.Red)

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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "Password",
                    color = if (emailIsFocused) Color.Red else Color.Gray
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color.Red
                    )
                }
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = {
                Text(
                    text = "Confirm Password",
                    color = if (emailIsFocused) Color.Red else Color.Gray
                )
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                        tint = Color.Red
                    )
                }
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

        Button(onClick = {
            if (password == confirmPassword) {
            authViewModel.signup(email,password)
            } else {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
            Text(text = "Create account", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate("login")
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
            Text(text = "Already have an account? Login", color = Color.Red)
        }
    }
}
