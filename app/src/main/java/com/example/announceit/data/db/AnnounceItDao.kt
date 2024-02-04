package com.example.announceit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.announceit.data.db.models.Announcement
import com.example.announceit.data.db.models.AnnouncementAggregate
import com.example.announceit.data.db.models.Attachment
import com.example.announceit.data.db.models.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnounceItDao {
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementAggregate>>

    @Query("SELECT * FROM courses ORDER BY name")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM announcements WHERE courseId = :courseId ORDER BY date DESC")
    fun getAnnouncementsByCourse(courseId: String): Flow<List<AnnouncementAggregate>>

    @Query("SELECT * FROM announcements WHERE isSaved = 1 ORDER BY date DESC")
    fun getSavedAnnouncements(): Flow<List<AnnouncementAggregate>>

    @Query("SELECT * FROM courses")
    fun getCourses(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAnnouncement(announcement: Announcement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourse(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttachment(attachment: Attachment)

    @Query("SELECT * FROM announcements WHERE id = :id")
    fun getAnnouncement(id: String): AnnouncementAggregate?

    @Query("SELECT * FROM courses WHERE id = :id")
    fun getCourse(id: String): Flow<Course?>

    @Query("SELECT * FROM announcements WHERE title LIKE '%'||:searchQuery||'%' OR body LIKE '%'||:searchQuery||'%'")
    fun getAnnouncementsSearch(searchQuery: String): List<AnnouncementAggregate>


}