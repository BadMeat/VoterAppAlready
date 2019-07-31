package com.voter.voterapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.voter.voterapp.model.UserVoter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private var userVoter: UserVoter? = null

    companion object{
        const val ID_USER = "ID_USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_login.setOnClickListener {
            if (loadUser()) {
                val session = getSharedPreferences("session", Context.MODE_PRIVATE)
                val editor = session.edit()
                val gson = Gson()
                val mJson = gson.toJson(userVoter)
                editor.putString("user", mJson)
                editor.apply()
                startActivity(Intent(baseContext, VoteActivity::class.java))
            }
        }

        button_exit.setOnClickListener {
            finish()
        }

        text_create_user.setOnClickListener {
            startActivity(Intent(baseContext, CreateUserActivity::class.java))
        }

        text_forget_pass.setOnClickListener {
            if(validateForgot()){
                loadUser()
                val intent = Intent(baseContext,ForgotActivity::class.java)
                intent.putExtra(ID_USER,userVoter?.id)
                startActivity(Intent(baseContext,ForgotActivity::class.java))
            }
        }
    }

    private fun validateForgot() : Boolean{
        val textUser = text_user.text
        if(textUser.isNullOrBlank()){
            Toast.makeText(baseContext,getString(R.string.alert_user),Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loadUser(): Boolean {
        var isValid = false
        val user = edit_user.text.toString().trim()
        val pass = edit_password.text.toString().trim()
        if (user.isEmpty()) {
            Toast.makeText(baseContext, resources.getString(R.string.alert_user), Toast.LENGTH_SHORT).show()
            isValid = false
            edit_user.requestFocus()
            return isValid
        }

        if (pass.isEmpty()) {
            Toast.makeText(baseContext, resources.getString(R.string.alert_password), Toast.LENGTH_SHORT).show()
            isValid = false
            edit_password.requestFocus()
            return isValid
        }

        database.use {
            val result = select(UserVoter.TABLE_USER)
                .whereArgs(
                    "(USER_NAME) = {user} AND (USER_PASSWORD) = {pass}",
                    "user" to user, "pass" to pass
                )
            val valid = result.parseList(classParser<UserVoter>())

            if (valid.isNotEmpty()) {
                isValid = true
                userVoter = valid[0]
            } else {
                Toast.makeText(baseContext, resources.getString(R.string.wrong_user), Toast.LENGTH_SHORT).show()
            }
        }
        return isValid
    }

    override fun onResume() {
        super.onResume()
        edit_password.text.clear()
        edit_user.text.clear()
    }
}
