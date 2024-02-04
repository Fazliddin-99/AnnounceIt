package com.example.announceit.ui.main_screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.announceit.ui.announcement_details.AnnouncementDetailsScreen
import com.example.announceit.ui.announcement_edit.AnnouncementEditScreen
import com.example.announceit.ui.announcements_by_course.AnnouncementsByCourse
import com.example.announceit.ui.announcements_thread.AnnouncementsThreadScreen
import com.example.announceit.ui.course_list.CourseListScreen
import com.example.announceit.ui.login.LoginScreen
import com.example.announceit.ui.saved_announcements.SavedAnnouncementsScreen
import com.example.announceit.ui.theme.AnnounceItTheme
import com.example.announceit.util.Routes
import com.example.announceit.util.slideInToLeft
import com.example.announceit.util.slideInToRight
import com.example.announceit.util.slideOutToLeft
import com.example.announceit.util.slideOutToRight

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val currentRoute = currentRoute(navController = navController)

            if (currentRoute in listOf(
                    Routes.ANNOUNCEMENTS_THREAD,
                    Routes.SAVED_ANNOUNCEMENTS,
                    Routes.COURSES_LIST
                )
            ) {
                NavigationBar {


                    BottomNavigationItem().bottomNavigationItems()
                        .forEachIndexed { index, navigationItem ->
                            NavigationBarItem(
                                selected = index == navigationSelectedItem,
                                label = {
                                    Text(navigationItem.label)
                                },
                                icon = {
                                    Icon(
                                        navigationItem.icon,
                                        contentDescription = navigationItem.label
                                    )
                                },
                                onClick = {
                                    navigationSelectedItem = index
                                    navController.navigate(navigationItem.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                }
            }
        }
    ) {

        AnnounceItTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                tonalElevation = 2.dp
            ) {
                val activity = (LocalContext.current as? Activity)

                NavHost(
                    navController = navController, startDestination = Routes.LOGIN
                ) {
                    composable(Routes.LOGIN) {
                        LoginScreen(onNavigate = {
                            navController.navigate(it.route)
                        })
                    }

                    composable(
                        Routes.ANNOUNCEMENTS_THREAD,
                        enterTransition = {
                            val route = initialState.destination.route
                            if (route == Routes.SAVED_ANNOUNCEMENTS || route == Routes.COURSES_LIST) slideInToRight() else null
                        },
                        exitTransition = {
                            val route = targetState.destination.route
                            if (route == Routes.SAVED_ANNOUNCEMENTS || route == Routes.COURSES_LIST) slideOutToLeft() else null
                        }) {
                        AnnouncementsThreadScreen(
                            onNavigate = {
                                navController.navigate(it.route)
                            },
                            onBackPressed = {
                                activity?.finish()
                            },
                            onPopBackStack = {
                                navController.popBackStack()
                            })
                    }

                    composable(Routes.SAVED_ANNOUNCEMENTS,
                        enterTransition = {
                            when (initialState.destination.route) {
                                Routes.ANNOUNCEMENTS_THREAD -> slideInToLeft()
                                Routes.COURSES_LIST -> slideInToRight()
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                Routes.ANNOUNCEMENTS_THREAD -> slideOutToRight()
                                Routes.COURSES_LIST -> slideOutToLeft()
                                else -> null
                            }
                        }) {
                        SavedAnnouncementsScreen(
                            onNavigate = {
                                navController.navigate(it.route)
                            },
                            onBackPressed = {
                                activity?.finish()
                            },
                            onPopBackStack = {
                                navController.popBackStack()
                            })
                    }

                    composable(Routes.COURSES_LIST,
                        enterTransition = {
                            val route = initialState.destination.route
                            if (route == Routes.SAVED_ANNOUNCEMENTS || route == Routes.ANNOUNCEMENTS_THREAD) slideInToLeft() else null
                        },
                        exitTransition = {
                            val route = targetState.destination.route
                            if (route == Routes.SAVED_ANNOUNCEMENTS || route == Routes.ANNOUNCEMENTS_THREAD) slideOutToRight() else null
                        }) {
                        CourseListScreen(
                            onNavigate = { navController.navigate(it.route) },
                            onBackPressed = { activity?.finish() },
                            onPopBackStack = { navController.popBackStack() })
                    }
                    composable("${Routes.ANNOUNCEMENTS_BY_COURSE}/{courseId}") {
                        AnnouncementsByCourse(
                            onNavigate = { navController.navigate(it.route) },
                            onPopBackStack = { navController.popBackStack() }
                        )
                    }

                    composable("${Routes.ANNOUNCEMENT_DETAILS}/{announcementId}") {
                        AnnouncementDetailsScreen(
                            onNavigate = { navController.navigate(it.route) },
                            onPopBackStack = { navController.popBackStack() }
                        )
                    }

                    composable("${Routes.ANNOUNCEMENT_EDIT}/{announcementId}") {
                        AnnouncementEditScreen(
                            onNavigate = { navController.navigate(it.route) },
                            onPopBackStack = { navController.popBackStack() })
                    }
                }

            }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}