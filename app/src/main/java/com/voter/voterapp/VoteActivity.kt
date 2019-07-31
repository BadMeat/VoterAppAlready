package com.voter.voterapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.voter.voterapp.model.UserVoter
import kotlinx.android.synthetic.main.activity_vote.*

class VoteActivity : AppCompatActivity() {

    private var mVoter: UserVoter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote)

        setSupportActionBar(main_toolbar)

        val gson = Gson()
        val mSetting = getSharedPreferences("session", Context.MODE_PRIVATE)
        val mUser = mSetting.getString("user", "missing")
        mVoter = gson.fromJson(mUser, UserVoter::class.java)
        text_user.text = resources.getString(R.string.hi_user, mVoter?.user)

        image_vote.setOnClickListener {
            startActivity(Intent(this, QuestionActivity::class.java))
        }
        image_result.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val isAdmin = mVoter?.role
        if (isAdmin.equals("Admin", true)) {
            menuInflater.inflate(R.menu.voter_menu, menu)
        } else {
            menuInflater.inflate(R.menu.user_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
            R.id.item_logout -> {
                val mSession = getSharedPreferences("session", Context.MODE_PRIVATE).edit()
                mSession.clear()
                mSession.apply()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val mSession = getSharedPreferences("session", Context.MODE_PRIVATE).contains("user")
        Log.d("mSession", "$mSession")
        if (!mSession) {
            finish()
        }
    }
}
