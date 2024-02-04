package com.example.announceit.ui.announcements_thread

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.announceit.R
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.db.models.AnnouncementAggregate
import com.example.announceit.util.makeString
import java.util.Date

@Composable
fun AnnouncementsThreadItem(
    modifier: Modifier = Modifier,
    announcement: AnnouncementAggregate,
    isSelected: Boolean = false,
    navigateToDetail: (String) -> Unit,
    onFavouriteClicked: (Announcement) -> Unit
) {

    Card(modifier = modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .semantics { selected = isSelected }
        .clickable { navigateToDetail(announcement.announcement.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = announcement.announcement.imageUrl,
                    placeholder = painterResource(id = R.drawable.camera),
                    contentDescription = "image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = announcement.course?.name ?: "",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = Date(announcement.announcement.date).makeString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        onFavouriteClicked(announcement.announcement)
                    },
                    modifier = Modifier.clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        tint = if (announcement.announcement.isSaved)
                            Color.Yellow else LocalContentColor.current,
                        contentDescription = "Favourite"
                    )
                }
            }

            Text(
                text = announcement.announcement.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            Text(
                text = announcement.announcement.body,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = announcement.course?.teacher ?: "",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}