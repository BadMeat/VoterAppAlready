package com.voter.voterapp.model

data class Question(
    val id: Long,
    val name: String?
) {
    companion object {
        const val TABLE_NAME = "QUESTION_TABLE"
        const val ID = "Q_ID"
        const val NAME = "Q_NAME"
    }
}