package com.voter.voterapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.voter.voterapp.model.*
import org.jetbrains.anko.db.*

class VoterDatabase(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "VoterApp.db", null, 2) {

    companion object {
        private var instance: VoterDatabase? = null

        fun getInstance(ctx: Context): VoterDatabase {
            if (instance == null) {
                instance = VoterDatabase(ctx)
            }
            return instance as VoterDatabase
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(
            UserVoter.TABLE_USER, true,
            UserVoter.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            UserVoter.NAME to TEXT + UNIQUE,
            UserVoter.PASSWORD to TEXT,
            UserVoter.ROLE to TEXT,
            UserVoter.SEC_QUESTION to TEXT,
            UserVoter.SEC_ANSWER to TEXT
        )

        db?.createTable(
            Question.TABLE_NAME, true,
            Question.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Question.NAME to TEXT + UNIQUE
        )

        db?.createTable(
            Answer.TABLE_NAME, true,
            Answer.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Answer.NAME to TEXT,
            Answer.TIPE to INTEGER
        )

        db?.createTable(
            ResultVote.TABLE_NAME, true,
            ResultVote.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            ResultVote.USER to TEXT,
            ResultVote.QUESTION to TEXT,
            ResultVote.ANSWER to TEXT
        )

        db?.createTable(
            HistoryVoter.TABLE_NAME, true,
            HistoryVoter.HIS_ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            HistoryVoter.HIS_USER to INTEGER,
            HistoryVoter.HIS_QUESTION to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion > newVersion) {
            db?.dropTable(UserVoter.TABLE_USER)
            db?.dropTable(Question.TABLE_NAME)
            db?.dropTable(Answer.TABLE_NAME)
            db?.dropTable(ResultVote.TABLE_NAME)
            db?.dropTable(HistoryVoter.TABLE_NAME)
        }
    }
}

val Context.database: VoterDatabase
    get() = VoterDatabase.getInstance(applicationContext)