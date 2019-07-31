package com.voter.voterapp

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.voter.voterapp.model.Answer
import com.voter.voterapp.model.Question
import kotlinx.android.synthetic.main.activity_create_user.button_save
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.db.insert

class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private val mEditText: MutableList<EditText> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        button_save.setOnClickListener(this)
        button_cancel.setOnClickListener(this)
        initTextView()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save -> {
                saveQuestion()
            }
            R.id.button_cancel -> {
                clearText()
            }
        }
    }

    private fun validateSave(name: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(baseContext, resources.getString(R.string.alert_question), Toast.LENGTH_SHORT).show()
            return false
        }
        for (i: EditText in mEditText) {
            if (i.text.isEmpty()) {
                Toast.makeText(baseContext, resources.getString(R.string.alert_asnwer), Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun saveQuestion() {
        val mEditName = edit_question.text.toString()
        var valid = true
        if (validateSave(mEditName)) {
            var mTempId: Long? = null
            database.use {
                try {
                    mTempId = insert(
                        Question.TABLE_NAME,
                        Question.NAME to mEditName
                    )
                    if (mTempId == -1L) {
                        Toast.makeText(
                            baseContext,
                            resources.getString(R.string.question_constraint),
                            Toast.LENGTH_SHORT
                        ).show()
                        valid = false
                    }
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            if (!valid) {
                edit_question.text.clear()
                edit_question.requestFocus()
                return
            }
            saveAnswer(mTempId?.toInt())
        }
    }

    private fun initTextView() {
        val layoutParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParam.bottomMargin = resources.getDimension(R.dimen.small_space).toInt()
        for (i in 0 until 3) {
            val mTempEditText = EditText(this)
            mTempEditText.hint = resources.getString(R.string.answer)
            mTempEditText.id = i
            mTempEditText.background = resources.getDrawable(R.drawable.border_line, null)
            mTempEditText.setPadding(resources.getDimension(R.dimen.small_space).toInt())
            mTempEditText.layoutParams = layoutParam
            mEditText.add(i, mTempEditText)
            answer_container.addView(mTempEditText)
        }
    }

    private fun saveAnswer(id: Int?) {
        for (i in 0 until mEditText.size) {
            database.use {
                try {
                    insert(
                        Answer.TABLE_NAME,
                        Answer.NAME to mEditText[i].text.toString(),
                        Answer.TIPE to id
                    )
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        Toast.makeText(baseContext, resources.getString(R.string.save_succes), Toast.LENGTH_SHORT).show()
        clearText()
    }

    private fun clearText() {
        edit_question.text.clear()
        for (i in 0 until mEditText.size) {
            mEditText[i].text.clear()
        }
    }
}
