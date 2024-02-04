package com.example.announceit.ui.course_list

import com.example.announceit.data.db.models.Course

sealed class CourseListEvent {
    data class OnCourseItemClick(val course: Course) : CourseListEvent()
}
