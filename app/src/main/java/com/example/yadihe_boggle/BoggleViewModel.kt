package com.example.yadihe_boggle

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.contracts.contract
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
        return getStringFromButtonsHit(buttonsHit)
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
            updateToastMessage("Correct! Score +$addScore")
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

    fun hitButton(row:Int, col:Int){
        buttonsHit.add(Pair(row,col))
        updateEnabledButtons()
    }

    /**
     * Private Functions:
     */
    fun updateToastMessage(message: String) {
        _toastMessage.value = message
    }
    private fun scoreWord(word: String):Int{
        var s=0;
        s+=(word.length-countVowels(word))
        s+=5*countVowels(word)
        if(containsSpecialCharacters(word)){
            s*=2
        }
        return s
    }
    private fun checkWord(word:String):Boolean{
        if(word in wordsSubmitted){
            updateToastMessage("You cannot submit the same word twice.")
            return false
        }
        if(word.length<4){
            updateToastMessage("Words must be at least 4 chars long.")
            return false
        }
        if(countVowels(word)<2){
            updateToastMessage("Words must contain at least two vowels.")
            return false
        }
        return true
    }

    fun submitWrongWord(){
        score-=10
        updateToastMessage("Incorrect! Score -10")
        buttonsHit.clear()
        updateEnabledButtons()
    }

    /**
     * Extract the current word from buttonsHit
     */
    private fun getStringFromButtonsHit(buttonsHit: List<Pair<Int, Int>>): String {
        var s=""
        for ((row, col) in buttonsHit) {
            s+=(letterArray[row][col])
        }
        return s
    }

    /**
     * Initialize the letters array randomly.
     * Called when new game starts.
     */
    private fun fillLetterArray(letterArray: Array<CharArray>) {

        val alphabet = "AAAABBCCCCDDDEEEEEEEFFFGGGHIIIIIJJJKKLLLMNNNOOOOPQRRRRSSSSTTTTUUVVWWXXYYYZ"
        for (i in letterArray.indices) {
            for (j in letterArray[i].indices) {
                val randomIndex = Random.nextInt(0, alphabet.length)
                letterArray[i][j] = alphabet[randomIndex]
            }
        }
        Log.d("ViewModel", "Set to new letter array")
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
            enabledButtons.clear()
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
    fun countVowels(input: String): Int {
        val vowels = setOf('A', 'E', 'I', 'O', 'U')
        var count = 0

        for (char in input) {
            if (char in vowels) {
                count++
            }
        }

        return count
    }

    fun containsSpecialCharacters(input: String): Boolean {
        val specialCharacters = setOf('S', 'Z', 'P', 'X', 'Q')

        for (char in input) {
            if (char in specialCharacters) {
                return true
            }
        }

        return false
    }


}