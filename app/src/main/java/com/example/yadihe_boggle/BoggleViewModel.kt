package com.example.yadihe_boggle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

private const val TAG = "BoggleViewModel"
class BoggleViewModel : ViewModel() {
    val SIZE_OF_GAME=4
    val letterArray = Array(SIZE_OF_GAME) { CharArray(SIZE_OF_GAME) }
    val buttonsHit: MutableList<Pair<Int, Int>> = mutableListOf()
    val buttonsUsed: MutableList<Pair<Int, Int>> = mutableListOf()
    val enabledButtons: MutableList<Pair<Int, Int>> = mutableListOf()
    val wordsSubmitted: MutableList<String> = mutableListOf()
    var score = 0

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    init {
        resetGame()
    }

    /**
     * Public Functions:
     */
    fun resetGame() {
        fillLetterArray(letterArray)

        buttonsHit.clear()
        buttonsUsed.clear()
        wordsSubmitted.clear()
        updateEnabledButtons()
        score = 0

    }

    fun clearCurrentWord(){
        buttonsHit.clear()
        updateEnabledButtons()
    }

    fun getCurrentWord():String{
        return getStringFromButtonsHit(buttonsUsed)
    }
    fun getCurrentScore():Int{
        return score
    }
    fun submitWord(){
        val currentWord=getCurrentWord()
        if(checkWord(currentWord)){
            wordsSubmitted.add(currentWord)
            val addScore=scoreWord(currentWord)
            score+=addScore
            updateToastMessage("Score +$addScore")
            buttonsUsed.addAll(buttonsHit)
            buttonsHit.clear()
            updateEnabledButtons()
        }else{
            score-=10
            updateToastMessage("Score -10")
            buttonsHit.clear()
            updateEnabledButtons()
        }
    }

    /**
     * Private Functions:
     */
    fun updateToastMessage(message: String) {
        _toastMessage.value = message
    }
    private fun scoreWord(word: String):Int{
        return 1
    }
    private fun checkWord(word:String):Boolean{
        return true
    }

    /**
     * Extract the current word from buttonsHit
     */
    private fun getStringFromButtonsHit(buttonsHit: List<Pair<Int, Int>>): String {
        val stringBuilder = StringBuilder()
        for ((row, col) in buttonsHit) {
            stringBuilder.append("$row$col")
        }
        return stringBuilder.toString()
    }

    /**
     * Initialize the letters array randomly.
     * Called when new game starts.
     */
    private fun fillLetterArray(letterArray: Array<CharArray>) {

        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        for (i in letterArray.indices) {
            for (j in letterArray[i].indices) {
                val randomIndex = Random.nextInt(0, alphabet.length)
                letterArray[i][j] = alphabet[randomIndex]
            }
        }
    }


    /**
     * Update enabledButtons according to the last hit button, buttonsHit and buttonsUsed
     */
    private fun updateEnabledButtons() {

        if(buttonsHit.isEmpty()){
            for (i in 0 until SIZE_OF_GAME) {
                for (j in 0 until SIZE_OF_GAME) {
                    enabledButtons.add(Pair(i, j))
                }
            }
        }else{
            // Add all positions adjacent to the last hit button to enabledButtons
            val lastHit = buttonsHit.last()
            val (lastHitRow, lastHitCol) = lastHit
            for (rowOffset in -1..1) {
                for (colOffset in -1..1) {
                    val newRow = lastHitRow + rowOffset
                    val newCol = lastHitCol + colOffset
                    // Check if the new position is within the bounds of the game board
                    if (newRow in 0 until SIZE_OF_GAME && newCol in 0 until SIZE_OF_GAME) {
                        enabledButtons.add(Pair(newRow, newCol))
                    }
                }
            }
        }
        enabledButtons.removeAll { it in buttonsHit || it in buttonsUsed }
    }

}