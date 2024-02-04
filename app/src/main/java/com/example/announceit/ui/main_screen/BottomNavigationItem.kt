package com.example.announceit.ui.main_screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.announceit.util.Routes

//initializing the data class with default parameters
data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {

    //function to get the list of bottomNavigationItems
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Filled.Home,
                route = Routes.ANNOUNCEMENTS_THREAD
            ),
            BottomNavigationItem(
                label = "Saved",
                icon = Icons.Filled.Favorite,
                route = Routes.SAVED_ANNOUNCEMENTS
            ),
            BottomNavigationItem(
                label = "Courses",
                icon = Icons.Filled.List,
                route = Routes.COURSES_LIST
            ),
        )
    }
}