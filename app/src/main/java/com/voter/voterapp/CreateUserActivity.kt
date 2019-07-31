package com.voter.voterapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.voter.voterapp.model.UserVoter
import kotlinx.android.synthetic.main.activity_create_user.*
import org.jetbrains.anko.db.insert

class CreateUserActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        button_save.setOnClickListener(this)
        button_exit.setOnClickListener(this)
        button_cancel.setOnClickListener(this)
        val spinnerAdapter =
            ArrayAdapter<String>(this, R.layout.item_spinner, resources.getStringArray(R.array.user_role))
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner)
        spiner_role.adapter = spinnerAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save -> {
                saveData()
            }
            R.id.button_cancel -> {
                resetText()
            }
            R.id.button_exit -> {
                this.finish()
            }
        }
    }

    private fun saveData() {
        val user = edit_user.text.toString()
        val password = edit_password.text.toString()
        val role = spiner_role.selectedItem.toString()
        val secQuestion = spinner_question.selectedItem.toString()
        val secAnswer = edit_sec_answer.text.toString()
        var valid = true
        if (validate(user, password, secQuestion)) {
            database.use {
                val result = insert(
                    UserVoter.TABLE_USER,
                    UserVoter.NAME to user,
                    UserVoter.PASSWORD to password,
                    UserVoter.ROLE to role,
                    UserVoter.SEC_QUESTION to secQuestion,
                    UserVoter.SEC_ANSWER to secAnswer
                )
                if (result == -1L) {
                    valid = false
                }
            }
            if (!valid) {
                Toast.makeText(baseContext, resources.getString(R.string.user_contraint), Toast.LENGTH_SHORT).show()
                edit_user.requestFocus()
                return
            } else {
                Toast.makeText(baseContext, resources.getString(R.string.save_succes), Toast.LENGTH_SHORT).show()
                resetText()
            }
        }
    }

    private fun validate(user: String, password: String, secQuestion: String): Boolean {
        if (user.isEmpty()) {
            Toast.makeText(baseContext, resources.getString(R.string.alert_user), Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(baseContext, resources.getString(R.string.alert_password), Toast.LENGTH_SHORT).show()
            return false
        }
        if (secQuestion.isEmpty()) {
            Toast.makeText(baseContext, resources.getString(R.string.alert_sec_question), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun resetText() {
        edit_user.text.clear()
        edit_password.text.clear()
        edit_sec_answer.text.clear()
    }
}
