package com.example.alumnimanagementsystemapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.R
import com.example.alumnimanagementsystemapp.ui.theme.AlumniManagementSystemAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlumniManagementSystemAppTheme {
                Scaffold { paddingValues ->
                    Navigation(
                        modifier = Modifier.padding(paddingValues),
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    val authState = authViewModel.authState.observeAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .statusBarsPadding()
            ) {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    DrawerContent(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .statusBarsPadding(),
            topBar = {
                Column {
                    TopBar(
                        onOpenDrawer = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        navController = navController
                    )
                }
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
fun ScreenContent(paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = 16.dp
        )
    ) {
        // Featured Alumni Section
        item {
            Text(
                text = "Featured Alumni",
                fontSize = 24.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Featured Alumni Cards
        items(3) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Alumni Name",
                        fontSize = 20.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Class of 2023",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Software Engineer at Tech Company",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Upcoming Events Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Upcoming Events",
                fontSize = 24.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Event Cards
        items(3) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Alumni Meetup 2024",
                        fontSize = 18.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "March 15, 2024 • 6:00 PM",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "IST Campus, Main Hall",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    onOpenDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp)),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Gray,
            navigationIconContentColor = Color.Red,
            actionIconContentColor = Color.Red
        ),
        windowInsets = WindowInsets(0.dp),
        title = {
            Text(
                text = "IST Alumni Network",
                color = Color.Red,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu",
                tint = Color.Red,
                modifier = Modifier
                    .clickable {
                        onOpenDrawer()
                    }
                    .padding(start = 16.dp, end = 8.dp)
                    .size(28.dp)
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Notifications",
                tint = Color.Red,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        navController.navigate("notification")
                    }
            )

            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Profile",
                tint = Color.Red,
                modifier = Modifier
                    .clickable {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    .padding(start = 8.dp, end = 16.dp)
                    .size(30.dp)
            )
        }
    )
}

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Drawer Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "IST Alumni Network",
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Items
        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = null, tint = Color.Red) },
            label = { 
                Text(
                    "Home",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = { 
                navController.navigate("main") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.Red) },
            label = {
                Text(
                    "Profile",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = { navController.navigate("profile") },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Notifications, contentDescription = null, tint = Color.Red) },
            label = {
                Text(
                    "Notifications",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = { navController.navigate("notification") },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Email, contentDescription = null, tint = Color.Red) },
            label = {
                Text(
                    "Messages",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = { /* Navigate to messages */ },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Logout, contentDescription = null, tint = Color.Red) },
            label = {
                Text(
                    "Logout",
                    color = Color.Red,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = {
                authViewModel.signout()
                navController.navigate("welcome") {
                    popUpTo("main") { inclusive = true }
                }
            },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
        )
    }
}