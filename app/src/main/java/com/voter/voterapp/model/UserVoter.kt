package com.voter.voterapp.model

data class UserVoter(
    val id: Long,
    val user: String?,
    val password: String?,
    val role: String?,
    val secQuestion : String?,
    val secAnswer : String?
) {
    companion object {
        const val TABLE_USER = "USER_TABLE"
        const val ID = "USER_ID"
        const val NAME = "USER_NAME"
        const val PASSWORD = "USER_PASSWORD"
        const val ROLE = "ROLE"
        const val SEC_QUESTION = "SEC_QUESTION"
        const val SEC_ANSWER = "SEC_ANSWER"
    }
}