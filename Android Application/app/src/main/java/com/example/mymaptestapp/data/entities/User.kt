package com.example.mymaptestapp.data.entities

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val patronymic: String,
    val phoneNumber: String,
    val role: String
) {
    companion object {
        fun from(map: Map<String, Any>): User {
            return User(
                id = map["userId"] as Int,
                firstName = map["firstName"] as String,
                lastName = map["lastName"] as String,
                patronymic = map["patronymic"] as String,
                phoneNumber = map["phoneNumber"] as String,
                role = map["role"] as String
            )
        }
    }
}