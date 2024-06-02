package com.example.mymaptestapp.data.entities

data class Account(
    val id: Int,
    val userId: Int,
    val login: String,
    val password: String
) {
    companion object {
        fun from(map: Map<String, Any>): Account {
            return Account(
                id = map["accountId"] as Int,
                userId = map["userId"] as Int,
                login = map["login"] as String,
                password = map["password"] as String
            )
        }
    }
}
