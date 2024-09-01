package com.example.alumnimanagementsystemapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier.fillMaxSize()
    ){
        //Background Image
        Image(painter = painterResource(id = R.drawable.img2),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
            )
    }

}

@Preview(showBackground = true, widthDp = 329, heightDp = 640)
@Composable
fun WelcomeScreenPreview(){
    WelcomeScreen()
}