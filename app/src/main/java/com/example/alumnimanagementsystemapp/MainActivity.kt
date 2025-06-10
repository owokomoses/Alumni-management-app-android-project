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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import kotlinx.coroutines.CoroutineScope
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
                        authViewModel = authViewModel,
                        drawerState = drawerState,
                        scope = scope
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
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = modifier
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
    scrollBehavior: TopAppBarScrollBehavior,
    authViewModel: AuthViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(vertical = 8.dp)
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
                val userProfile by authViewModel.userProfileState.collectAsState()
                val currentUser = authViewModel.currentUser
                val displayName = userProfile.name.ifEmpty { currentUser?.displayName ?: "IST Alumni Network" }
                Text(
                    text = displayName,
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
                // Notification Icon
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Red,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Screen.Notifications.route) {
                                launchSingleTop = true
                            }
                        }
                        .padding(end = 8.dp)
                        .size(28.dp)
                )
                
                // Profile Icon/Image
                val userProfile by authViewModel.userProfileState.collectAsState()
                val currentUser = authViewModel.currentUser
                val displayName = userProfile.name.ifEmpty { currentUser?.displayName ?: "" }
                
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = 0.1f))
                        .clickable {
                            navController.navigate("profile") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .padding(start = 8.dp, end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (userProfile.profileImageUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(userProfile.profileImageUrl),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (displayName.isNotEmpty()) {
                        Text(
                            text = displayName.first().toString().uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.Red,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope
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
            selected = navController.currentDestination?.route == Screen.Home.route,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Gray.copy(alpha = 0.1f),
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = Color.Red,
                unselectedIconColor = Color.Red,
                selectedTextColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        // Only show Users navigation item for admin users
        val userProfile by authViewModel.userProfileState.collectAsState()
        if (userProfile.role == "admin") {
            NavigationDrawerItem(
                icon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.Red) },
                label = {
                    Text(
                        "Users",
                        color = Color.Gray,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                selected = navController.currentDestination?.route == Screen.Users.route,
                onClick = { 
                    scope.launch {
                        drawerState.close()
                        navController.navigate(Screen.Users.route) {
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color.Gray.copy(alpha = 0.1f),
                    unselectedContainerColor = Color.Transparent,
                    selectedIconColor = Color.Red,
                    unselectedIconColor = Color.Red,
                    selectedTextColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }

        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Article, contentDescription = null, tint = Color.Red) },
            label = {
                Text(
                    "Posts",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = navController.currentDestination?.route == Screen.Posts.route,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Screen.Posts.route) {
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Gray.copy(alpha = 0.1f),
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = Color.Red,
                unselectedIconColor = Color.Red,
                selectedTextColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Assignment, contentDescription = null, tint = Color.Red) },
            label = {
                Text(
                    "Applications",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = navController.currentDestination?.route == Screen.Applications.route,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Screen.Applications.route) {
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Gray.copy(alpha = 0.1f),
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = Color.Red,
                unselectedIconColor = Color.Red,
                selectedTextColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
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
                scope.launch {
                    drawerState.close()
                    authViewModel.signout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Gray.copy(alpha = 0.1f),
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = Color.Red,
                unselectedIconColor = Color.Red,
                selectedTextColor = Color.Red,
                unselectedTextColor = Color.Red
            )
        )
    }
}

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Users : Screen("users")
    object Posts : Screen("posts")
    object Notifications : Screen("notifications")
    object JobDetails : Screen("job_details/{postId}") {
        fun createRoute(postId: String) = "job_details/$postId"
    }
    object JobPostDetail : Screen("job_post_detail/{postId}") {
        fun createRoute(postId: String) = "job_post_detail/$postId"
    }
    object JobApplication : Screen("job_application/{jobId}") {
        fun createRoute(jobId: String) = "job_application/$jobId"
    }
    object ViewApplication : Screen("view_application/{applicationId}") {
        fun createRoute(applicationId: String) = "view_application/$applicationId"
    }
    object Applications : Screen("applications")
}