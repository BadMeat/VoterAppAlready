package com.voter.voterapp.model

/**
 * Created by Bencoleng on 10/07/2019.
 */
data class ResultVote(
    val id: Long,
    val user: String?,
    val question: String,
    val answer: String
) {
    companion object {
        const val TABLE_NAME = "RESULT_TABLE"
        const val ID = "R_ID"
        const val USER = "R_USER"
        const val QUESTION = "R_QUESTION"
        const val ANSWER = "R_ANSWER"
    }
}