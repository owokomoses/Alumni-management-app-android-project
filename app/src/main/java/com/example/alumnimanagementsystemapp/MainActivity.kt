package com.example.alumnimanagementsystemapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
fun Screen(modifier: Modifier = Modifier,navController: NavController, authViewModel: AuthViewModel){

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent()
            }
        }
    ) {

        Scaffold(
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column {
                    Spacer(modifier = Modifier.height(25.dp))

                    TopBar(
                        onOpenDrawer = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            }
        ) { paddingValues ->
            ScreenContent(
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun ScreenContent(paddingValues: PaddingValues){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() +16.dp
        )
    ) {
        items(10) {
            Box (
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.inversePrimary)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
){
    TopAppBar(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(100.dp)),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        windowInsets = WindowInsets(top = 0.dp),
        title = {
            Text(text = "Search for Jobs",
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                fontSize = 17.sp
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu",
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
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
            )

            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(30.dp)
            )
        }
    )
}

@Composable
fun DrawerContent(modifier: Modifier = Modifier){
    Text(
        text = "App Name",
        fontSize = 24.sp,
        modifier = Modifier.padding(16.dp)
    )

    HorizontalDivider()

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Account"
            )
        },
        label = {
            Text(
                text = "Account",
                fontSize = 17.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Notifications"
            )
        },
        label = {
            Text(
                text = "Notifications",
                fontSize = 17.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Email,
                contentDescription = "Email"
            )
        },
        label = {
            Text(
                text = "Email",
                fontSize = 17.sp,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )

}