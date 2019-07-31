package com.voter.voterapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.voter.voterapp.model.UserVoter
import kotlinx.android.synthetic.main.activity_forgot.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class ForgotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        val userId = intent.getLongExtra(MainActivity.ID_USER, 0)
        text_question.text = loadQuestion(userId)?.secQuestion
    }

    private fun loadQuestion(id: Long): UserVoter? {
        var voter: UserVoter? = null
        database.use {
            val result = select(UserVoter.TABLE_USER).whereArgs("${UserVoter.ID} = {id}", "id" to id)
            val e = result.parseSingle(classParser<UserVoter>())
            voter = e
        }
        return voter
    }
}
