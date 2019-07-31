package com.voter.voterapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.voter.voterapp.model.*
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.below
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
//        loadResult()
        loadAllResultQuestion()
    }


    private fun allUser(question: Long): Int {
        var resultSize = 0
        var mListUser: List<UserVoter>? = null
        database.use {
            val result = select(UserVoter.TABLE_USER)
            mListUser = result.parseList(classParser())
            resultSize = mListUser!!.size
        }
        return resultSize
    }

    private fun getUserSize(question: Long): Int {
        var mFinalResult = 0
        var mReturn: UserSize?
        database.use {
            val result = select(HistoryVoter.TABLE_NAME)
                .column("count(*)")
                .whereArgs(
                    "HIS_QUESTION = {question}",
                    "question" to question
                )
            mReturn = result.parseSingle(classParser())
            if (mReturn != null) {
                mFinalResult = mReturn!!.userSize
            }
        }
        return mFinalResult
    }

    private fun loadAllResultQuestion() {
        val paramsProgress = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val mTextRelative = RelativeLayout.LayoutParams(
            130,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        /**
         * All Questiion
         */
        database.use {
            val mResult = select(Question.TABLE_NAME)
            val mListResult = mResult.parseList(classParser<Question>())
            for (i in 0 until mListResult.size) {
                val mQuestion = TextView(baseContext)

                //---- Question TextView --- //
                mQuestion.id = i
                mQuestion.text = mListResult[i].name
                mQuestion.textSize = 20f
                mQuestion.layoutParams = paramsProgress
                main_container.addView(mQuestion)

                /**
                 * All Answer
                 */
                database.use {
                    val mResultAnswer = select(Answer.TABLE_NAME)
                        .whereArgs(
                            "ANSWER_TIPE = {question}",
                            "question" to mListResult[i].id
                        )
                    val mListAnswer = mResultAnswer.parseList(classParser<Answer>())
                    for (q in 0 until mListAnswer.size) {

                        val mRelativeLayout = RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )

                        mRelativeLayout.below(mQuestion)

                        val relativeLay = RelativeLayout(baseContext)
                        relativeLay.layoutParams = mRelativeLayout

                        //---- Progressbar --- //
                        val mProgressBar = ProgressBar(baseContext, null, android.R.attr.progressBarStyleHorizontal)
                        paramsProgress.marginStart = 16
                        paramsProgress.marginEnd = 16
                        mProgressBar.scaleY = 5f
                        mProgressBar.layoutParams = paramsProgress
                        mProgressBar.progressTintList = ColorStateList.valueOf(getColor(R.color.colorSkyBlue))
//                        main_container.addView(mProgressBar)
                        relativeLay.addView(mProgressBar)

                        //---- Answer TextView --- //
                        val mAnswer = TextView(baseContext)
                        mAnswer.text = mListAnswer[q].name
                        mAnswer.layoutParams = paramsProgress
//                        main_container.addView(mAnswer)
                        relativeLay.addView(mAnswer)
                        main_container.addView(relativeLay)

                        val mVoters = TextView(baseContext)
                        mVoters.layoutParams = paramsProgress
                        mVoters.text = resources.getString(R.string.voters, 0)
                        main_container.addView(mVoters)

                        val mTextCount = TextView(baseContext)
                        mTextRelative.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                        mTextRelative.marginEnd = resources.getDimension(R.dimen.standard_space).toInt()
                        mTextCount.layoutParams = mTextRelative
                        mTextCount.text = resources.getString(R.string.zero)
                        mTextCount.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
                        relativeLay.addView(mTextCount)

                        /**
                         * All Count
                         */
                        database.use {
                            val data = select(ResultVote.TABLE_NAME)
                                .column(ResultVote.ANSWER)
                                .column("COUNT(${ResultVote.ANSWER})")
                                .whereArgs(
                                    "R_QUESTION = {question} AND R_ANSWER = {answer}",
                                    "question" to mListResult[i].name!!, "answer" to mListAnswer[q].name!!
                                )
                                .groupBy(ResultVote.ANSWER)
                            val mListVoter = data.parseList(classParser<ResultWrapper>())
                            for (w in 0 until mListVoter.size) {

                                database.use {
                                    val mUserResult = select(ResultVote.TABLE_NAME)
                                        .whereArgs(
                                            "R_QUESTION = {question} AND R_ANSWER = {answer}",
                                            "question" to mListResult[i].name!!, "answer" to mListAnswer[q].name!!
                                        )
                                    val mUserList = mUserResult.parseList(classParser<ResultVote>())
                                    var allUser = ""
                                    for (p in 0 until mUserList.size - 1) {
                                        allUser += "${mUserList[p].user}, "
                                    }
                                    val mTempAnswer: Double = mListVoter[w].answerCount!!.toDouble()
//                                    val mTemp: Double = mTempAnswer.div(allUser(mListResult[i].id))
                                    val mTemp: Double = mTempAnswer.div(getUserSize(mListResult[i].id))
                                    val mCount = mTemp * 100
                                    allUser += "${mUserList[mUserList.size - 1].user}"

                                    mProgressBar.progress = mCount.toInt()

                                    //---- TextCount TextView --- //
                                    mTextCount.text = resources.getString(R.string.presentase, mCount.toInt())
                                    mVoters.text = resources.getString(R.string.voters, mUserList.size)
                                }
                            }
                        }
                    }
                }
                val linearParam = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2
                )
                linearParam.topMargin = resources.getDimension(R.dimen.standard_space).toInt()
                val mLinearLayout = LinearLayout(baseContext)
                mLinearLayout.setBackgroundColor(Color.BLACK)
                mLinearLayout.layoutParams = linearParam
                main_container.addView(mLinearLayout)
            }
        }
    }
}
