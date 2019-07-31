package com.voter.voterapp

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.voter.voterapp.model.Answer
import com.voter.voterapp.model.Question
import kotlinx.android.synthetic.main.activity_question.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class QuestionActivity : AppCompatActivity() {

    var questionData: List<Question> = mutableListOf()
    private var answerData: List<Answer> = mutableListOf()
    private var answerList: MutableList<List<Answer>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        loadDataQuestion()
        loadDataAnswer()
        val pageAdapter = MyPageAdapter(supportFragmentManager)
        view_pagger.adapter = pageAdapter
        setSupportActionBar(main_toolbar)
        tabs_main.setupWithViewPager(view_pagger)
    }

    private fun loadDataQuestion() {
        database.use {
            val result = select(Question.TABLE_NAME)
            questionData = result.parseList(classParser())
        }

    }

    private fun loadDataAnswer() {
        for (i in 0 until questionData.size) {
            database.use {
                val result = select(Answer.TABLE_NAME)
                    .whereArgs(
                        "ANSWER_TIPE = {question_id}",
                        "question_id" to questionData[i].id
                    )
                answerData = result.parseList(classParser())
                answerList.add(i, answerData)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_logout -> {
                val mSession = getSharedPreferences("session", Context.MODE_PRIVATE).edit()
                mSession.clear()
                mSession.apply()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class MyPageAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return QuestionFragment.newInstance(questionData[position], answerList[position])
        }

        override fun getCount(): Int {
            val questionDataCount = questionData.size
            if (questionDataCount > 0) {
                return questionDataCount
            }
            return 0
        }

        /**
         * For Title Tab
         */
        override fun getPageTitle(position: Int): CharSequence? {
            return questionData[position].name
        }
    }
}
