package com.example.quizz

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    // Modèle de données pour une question
    data class Question(
        val text: String,
        val imageResId: Int,
        val choices: List<String>,
        val correctIndex: Int,
        val contentDescription: String
    )

    // Liste des 5 questions statiques
    private lateinit var questions: List<Question>

    // États du jeu
    private var currentIndex = 0
    private var score = 0
    private var hasAnswered = false
    private var isGameFinished = false

    // Vues UI
    private lateinit var txtTitre: TextView
    private lateinit var txtScore: TextView
    private lateinit var progressQuiz: ProgressBar
    private lateinit var txtProgression: TextView
    private lateinit var layoutQuiz: LinearLayout
    private lateinit var txtNumeroQuestion: TextView
    private lateinit var imgQuestion: ImageView
    private lateinit var txtQuestion: TextView
    private lateinit var btnChoix1: Button
    private lateinit var btnChoix2: Button
    private lateinit var btnChoix3: Button
    private lateinit var txtFeedback: TextView
    private lateinit var btnSuivant: Button
    private lateinit var layoutResultat: LinearLayout
    private lateinit var txtResultatFinal: TextView
    private lateinit var btnRejouer: Button

    companion object {
        private const val KEY_CURRENT_INDEX = "key_current_index"
        private const val KEY_SCORE = "key_score"
        private const val KEY_HAS_ANSWERED = "key_has_answered"
        private const val KEY_IS_GAME_FINISHED = "key_is_game_finished"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initQuestions()
        initViews()
        initListeners()

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX, 0)
            score = savedInstanceState.getInt(KEY_SCORE, 0)
            hasAnswered = savedInstanceState.getBoolean(KEY_HAS_ANSWERED, false)
            isGameFinished = savedInstanceState.getBoolean(KEY_IS_GAME_FINISHED, false)

            if (isGameFinished) {
                showFinalResult()
            } else {
                displayQuestion()
                if (hasAnswered) {
                    lockChoicesAfterAnswer()
                }
            }
        } else {
            resetGame()
        }
    }

    private fun initQuestions() {
        questions = listOf(
            Question(
                text = "Quelle est cette figure géométrique à 3 côtés ?",
                imageResId = R.drawable.triangle,
                choices = listOf("Carré", "Triangle", "Cercle"),
                correctIndex = 1,
                contentDescription = getString(R.string.desc_triangle)
            ),
            Question(
                text = "Comment nomme-t-on cette figure géométrique à 4 côtés égaux ?",
                imageResId = R.drawable.carre,
                choices = listOf("Carré", "Rectangle", "Triangle"),
                correctIndex = 0,
                contentDescription = getString(R.string.desc_carre)
            ),
            Question(
                text = "Quelle est cette figure géométrique parfaitement ronde ?",
                imageResId = R.drawable.cercle,
                choices = listOf("Triangle", "Cercle", "Étoile"),
                correctIndex = 1,
                contentDescription = getString(R.string.desc_cercle)
            ),
            Question(
                text = "Quelle est cette forme à plusieurs branches souvent observée dans le ciel ?",
                imageResId = R.drawable.etoile,
                choices = listOf("Carré", "Cercle", "Étoile"),
                correctIndex = 2,
                contentDescription = getString(R.string.desc_etoile)
            ),
            Question(
                text = "Quelle est cette figure géométrique à 4 côtés dont les côtés opposés sont égaux ?",
                imageResId = R.drawable.rectangle,
                choices = listOf("Rectangle", "Carré", "Cercle"),
                correctIndex = 0,
                contentDescription = getString(R.string.desc_rectangle)
            )
        )
    }

    private fun initViews() {
        txtTitre = findViewById(R.id.txt_titre)
        txtScore = findViewById(R.id.txt_score)
        progressQuiz = findViewById(R.id.progress_quiz)
        txtProgression = findViewById(R.id.txt_progression)
        layoutQuiz = findViewById(R.id.layout_quiz)
        txtNumeroQuestion = findViewById(R.id.txt_numero_question)
        imgQuestion = findViewById(R.id.img_question)
        txtQuestion = findViewById(R.id.txt_question)
        btnChoix1 = findViewById(R.id.btn_choix1)
        btnChoix2 = findViewById(R.id.btn_choix2)
        btnChoix3 = findViewById(R.id.btn_choix3)
        txtFeedback = findViewById(R.id.txt_feedback)
        btnSuivant = findViewById(R.id.btn_suivant)
        layoutResultat = findViewById(R.id.layout_resultat)
        txtResultatFinal = findViewById(R.id.txt_resultat_final)
        btnRejouer = findViewById(R.id.btn_rejouer)
    }

    private fun initListeners() {
        val choiceClickListener = View.OnClickListener { view ->
            if (hasAnswered) return@OnClickListener
            val selectedIndex = when (view.id) {
                R.id.btn_choix1 -> 0
                R.id.btn_choix2 -> 1
                R.id.btn_choix3 -> 2
                else -> -1
            }
            if (selectedIndex != -1) {
                checkAnswer(selectedIndex)
            }
        }

        btnChoix1.setOnClickListener(choiceClickListener)
        btnChoix2.setOnClickListener(choiceClickListener)
        btnChoix3.setOnClickListener(choiceClickListener)

        btnSuivant.setOnClickListener {
            goToNextQuestion()
        }

        btnRejouer.setOnClickListener {
            resetGame()
        }
    }

    private fun resetGame() {
        currentIndex = 0
        score = 0
        hasAnswered = false
        isGameFinished = false

        layoutResultat.visibility = View.GONE
        layoutQuiz.visibility = View.VISIBLE

        updateScoreDisplay()
        displayQuestion()
    }

    private fun displayQuestion() {
        if (currentIndex >= questions.size) return

        val q = questions[currentIndex]
        hasAnswered = false

        txtNumeroQuestion.text = getString(R.string.question_number, currentIndex + 1)
        imgQuestion.setImageResource(q.imageResId)
        imgQuestion.contentDescription = q.contentDescription
        txtQuestion.text = q.text

        btnChoix1.text = q.choices[0]
        btnChoix2.text = q.choices[1]
        btnChoix3.text = q.choices[2]

        setChoiceButtonEnabled(true)
        resetButtonColors()

        txtFeedback.visibility = View.GONE
        btnSuivant.visibility = View.GONE

        progressQuiz.progress = currentIndex
        txtProgression.text = getString(R.string.progression_label, currentIndex)
    }

    private fun checkAnswer(selectedIndex: Int) {
        hasAnswered = true
        val q = questions[currentIndex]
        val isCorrect = (selectedIndex == q.correctIndex)

        if (isCorrect) {
            score++
            txtFeedback.text = getString(R.string.correct_feedback)
            txtFeedback.setTextColor(Color.parseColor("#2E7D32")) // Vert foncé
        } else {
            val correctChoiceText = q.choices[q.correctIndex]
            txtFeedback.text = getString(R.string.incorrect_feedback, correctChoiceText)
            txtFeedback.setTextColor(Color.parseColor("#C62828")) // Rouge foncé
        }

        txtFeedback.visibility = View.VISIBLE

        highlightChoices(q.correctIndex, selectedIndex)
        setChoiceButtonEnabled(false)

        updateScoreDisplay()
        progressQuiz.progress = currentIndex + 1
        txtProgression.text = getString(R.string.progression_label, currentIndex + 1)

        if (currentIndex == questions.size - 1) {
            btnSuivant.text = getString(R.string.btn_see_result)
        } else {
            btnSuivant.text = getString(R.string.btn_next)
        }
        btnSuivant.visibility = View.VISIBLE
    }

    private fun lockChoicesAfterAnswer() {
        setChoiceButtonEnabled(false)
        txtFeedback.visibility = View.VISIBLE
        btnSuivant.visibility = View.VISIBLE
        if (currentIndex == questions.size - 1) {
            btnSuivant.text = getString(R.string.btn_see_result)
        } else {
            btnSuivant.text = getString(R.string.btn_next)
        }
    }

    private fun highlightChoices(correctIndex: Int, selectedIndex: Int) {
        val buttons = listOf(btnChoix1, btnChoix2, btnChoix3)
        for (i in buttons.indices) {
            if (i == correctIndex) {
                buttons[i].setBackgroundColor(Color.parseColor("#4CAF50")) // Vert
            } else if (i == selectedIndex) {
                buttons[i].setBackgroundColor(Color.parseColor("#F44336")) // Rouge
            } else {
                buttons[i].setBackgroundColor(Color.parseColor("#B0BEC5")) // Gris neutre
            }
        }
    }

    private fun resetButtonColors() {
        val buttons = listOf(btnChoix1, btnChoix2, btnChoix3)
        for (btn in buttons) {
            btn.setBackgroundColor(Color.parseColor("#3F51B5"))
        }
    }

    private fun setChoiceButtonEnabled(enabled: Boolean) {
        btnChoix1.isEnabled = enabled
        btnChoix2.isEnabled = enabled
        btnChoix3.isEnabled = enabled
    }

    private fun goToNextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            displayQuestion()
        } else {
            showFinalResult()
        }
    }

    private fun showFinalResult() {
        isGameFinished = true
        layoutQuiz.visibility = View.GONE
        layoutResultat.visibility = View.VISIBLE

        txtResultatFinal.text = getString(R.string.final_result_score, score) + "\n\n" + getResultMessage(score)
    }

    private fun getResultMessage(finalScore: Int): String {
        return when (finalScore) {
            5 -> getString(R.string.final_result_message_perfect)
            in 3..4 -> getString(R.string.final_result_message_good)
            in 1..2 -> getString(R.string.final_result_message_average)
            else -> getString(R.string.final_result_message_low)
        }
    }

    private fun updateScoreDisplay() {
        txtScore.text = getString(R.string.score_label, score)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_INDEX, currentIndex)
        outState.putInt(KEY_SCORE, score)
        outState.putBoolean(KEY_HAS_ANSWERED, hasAnswered)
        outState.putBoolean(KEY_IS_GAME_FINISHED, isGameFinished)
    }
}
