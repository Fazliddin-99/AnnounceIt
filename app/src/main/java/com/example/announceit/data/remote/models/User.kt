package com.example.announceit.data.remote.models

data class User(
    val id: String,
    val email: String,
    val name: String,
    val surname: String,
    val type: UserType
)

enum class UserType(private val value: Int) {
    TEACHER(1), STUDENT(2)
}