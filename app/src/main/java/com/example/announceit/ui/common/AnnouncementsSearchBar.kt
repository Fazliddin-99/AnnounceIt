package com.example.announceit.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onQueryValueChange: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                ,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search announcements",
                    modifier = Modifier.padding(start = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            value = searchQuery,
            placeholder = { Text(text = "Search announcements") },
            onValueChange = {
                onQueryValueChange(it)
            }
        )
    }
}