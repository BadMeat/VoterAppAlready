package com.voter.voterapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voter.voterapp.model.Answer
import com.voter.voterapp.model.Question
import kotlinx.android.synthetic.main.activity_answer.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class AnswerActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        loadQuestionData()
        button_save.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save -> {
                saveData()
            }
        }
    }

    private fun loadQuestionData() {
        val questionList: MutableList<String> = mutableListOf()
        database.use {
            val result = select(Question.TABLE_NAME)
            val data = result.parseList(classParser<Question>())
            if (data.isNotEmpty()) {
                for (i in 0 until data.size) {
                    questionList.add(i, data[i].name!!)
                }
            }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, questionList)
        spinner_question.adapter = adapter
    }

    private fun saveData() {

        var result: List<Question> = mutableListOf()

        database.use {
            val select = select(Question.TABLE_NAME).whereArgs(
                "Q_NAME = {name}",
                "name" to spinner_question.selectedItem.toString()
            )
            result = select.parseList(classParser())
        }
        val editAnswer = edit_answer.text.toString()
        database.use {
            insert(
                Answer.TABLE_NAME,
                Answer.NAME to editAnswer,
                Answer.TIPE to result[0].id

            )
            Toast.makeText(baseContext, resources.getString(R.string.save_succes), Toast.LENGTH_SHORT).show()
        }
    }
}
