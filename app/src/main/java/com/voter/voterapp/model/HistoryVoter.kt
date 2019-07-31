package com.voter.voterapp.model

/**
 * Created by Bencoleng on 10/07/2019.
 */
data class HistoryVoter(
    val id: Long,
    val user: Long,
    val question: Long
) {
    companion object {
        const val TABLE_NAME = "HISTORY_TABLE"
        const val HIS_ID = "HIS_ID"
        const val HIS_USER = "HIS_USER"
        const val HIS_QUESTION = "HIS_QUESTION"
    }
}