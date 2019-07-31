package com.voter.voterapp.model

data class Answer(
    val id: Long,
    val name: String?,
    val tipe: Int?
) {
    companion object {
        const val TABLE_NAME = "ANSWER_TABLE"
        const val ID = "ANSWER_ID"
        const val NAME = "ANSWER_NAME"
        const val TIPE = "ANSWER_TIPE"
    }
}