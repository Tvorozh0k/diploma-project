package com.example.mymaptestapp.data

import com.example.mymaptestapp.utils.JSONSerializer
import okio.ByteString.Companion.decodeBase64
import java.util.Date


data class JwtToken(
    val sub: String,
    val role: String,
    val iat: Date,
    val exp: Date
) {
    companion object {
        fun fromToken(token: String): JwtToken {
            val parts = token.split(".")
            val userData = JSONSerializer.jsonToMap(
                String(
                    parts[1].decodeBase64()!!.toByteArray(),
                    Charsets.UTF_8
                )
            )

            return JwtToken(
                sub = userData["sub"].toString(),
                role = userData["role"].toString(),
                iat = Date((userData["iat"].toString().toLongOrNull() ?: 0) * 1000),
                exp = Date((userData["exp"].toString().toLongOrNull() ?: 0) * 1000)
            )
        }

        fun isExpired(token: JwtToken): Boolean {
            return token.exp.before(Date())
        }
    }
}