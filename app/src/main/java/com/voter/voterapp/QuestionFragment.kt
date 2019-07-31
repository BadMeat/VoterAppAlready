package com.voter.voterapp


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.voter.voterapp.model.*
import kotlinx.android.synthetic.main.fragment_question.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select


class QuestionFragment : Fragment(), View.OnClickListener {

    private var mVoter: UserVoter? = null
    private var mQuestion: Question? = null
    private var mAnswer: List<Answer> = mutableListOf()
    private var mRadioButton: MutableList<RadioButton> = mutableListOf()
    private var maxPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        maxPage = (activity as QuestionActivity).questionData.size - 1

        /**
         * Question
         */

        val myQuestionKey = arguments!!.getString("question")
        mQuestion = Gson().fromJson(myQuestionKey, Question::class.java)
        text_question.text = mQuestion?.name

        /**
         * Answer
         */
        val mAnswerKey = arguments!!.getString("answer")
        val type = object : TypeToken<List<Answer>>() {}.type
        mAnswer = Gson().fromJson(mAnswerKey, type)
        for (i in 0 until mAnswer.size) {
            val buttonGroup = RadioButton(context)
            buttonGroup.text = mAnswer[i].name
            mRadioButton.add(i, buttonGroup)
            radio_group.addView(buttonGroup)
        }

        val mSession = context?.getSharedPreferences("session", Context.MODE_PRIVATE)
        val mUser = mSession?.getString("user", "missing")
        mVoter = Gson().fromJson(mUser, UserVoter::class.java)
        button_vote.setOnClickListener(this)
    }

    private fun validateUser(): Boolean {
        var resultValidate = true
        context?.database?.use {
            val result = select(HistoryVoter.TABLE_NAME)
            val dataHistory = result.parseList(classParser<HistoryVoter>())
            for (i: HistoryVoter in dataHistory) {
                if (i.user == mVoter?.id && i.question == mQuestion?.id) {
                    resultValidate = false
                }
            }
        }
        return resultValidate
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_vote -> {
                if (validateUser()) {
                    val idx = radio_group.checkedRadioButtonId
                    val radio: RadioButton = radio_group.findViewById(idx)
                    val indexe = radio_group.indexOfChild(radio)
                    context?.database?.use {
                        insert(
                            ResultVote.TABLE_NAME,
                            ResultVote.USER to mVoter?.user,
                            ResultVote.QUESTION to mQuestion?.name,
                            ResultVote.ANSWER to mAnswer[indexe].name
                        )
                    }

                    context?.database?.use {
                        insert(
                            HistoryVoter.TABLE_NAME,
                            HistoryVoter.HIS_USER to mVoter?.id,
                            HistoryVoter.HIS_QUESTION to mQuestion?.id
                        )
                    }
                    Toast.makeText(context, resources.getString(R.string.save_succes), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, resources.getString(R.string.already_vote), Toast.LENGTH_SHORT).show()
                }
                radio_group.clearCheck()
            }
        }
    }

    companion object {
        fun newInstance(question: Question, answer: List<Answer>): Fragment {
            val fragment = QuestionFragment()
            val args = Bundle()
            args.putString("question", Gson().toJson(question))
            args.putString("answer", Gson().toJson(answer))
            fragment.arguments = args
            return fragment
        }
    }
}
