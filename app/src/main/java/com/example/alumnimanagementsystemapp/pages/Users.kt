package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.Screen


@Composable
fun Users(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    Screen(
        navController = navController,
        authViewModel = authViewModel
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Users list content goes here
            item {
                Text("Users Screen Content", modifier = Modifier.padding(16.dp))
            }
        }
    }
}


