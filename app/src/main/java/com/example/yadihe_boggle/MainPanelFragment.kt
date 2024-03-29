package com.example.yadihe_boggle

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.yadihe_boggle.databinding.MainPanelBinding

interface MainPanelCommunicator {
    fun getLetterArray(): Array<CharArray>
    fun clickButton(row: Int,col: Int)

    fun getEnabledButtons():MutableList<Pair<Int, Int>>

    fun getCurrWord():String

    fun submit()

    fun updateScore()

    fun clear()



}
class MainPanelFragment: Fragment() {
    private lateinit var binding: MainPanelBinding
    private var activityListener: MainPanelCommunicator? = null
    val SIZE_OF_GAME=4

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            MainPanelBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateButtonArray()
        binding.buttonSubmit.setOnClickListener{
            activityListener?.submit()
            updateUI()
            activityListener?.updateScore()
        }
        binding.buttonClear.setOnClickListener{
            activityListener?.clear()
            updateUI()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainPanelCommunicator) {
            activityListener = context
        } else {
            throw RuntimeException("$context must implement DataProvider")
        }
    }

    fun inflateButtonArray(){
        val letterArray= activityListener?.getLetterArray()
        if (letterArray != null) {
            // Clear existing buttons in the button_container
            binding.buttonContainer.removeAllViews()

            // Iterate over each row of the letterArray
            for (row in letterArray.indices) {
                // Create a new horizontal LinearLayout for each row
                val rowLayout = LinearLayout(requireContext())
                rowLayout.orientation = LinearLayout.HORIZONTAL
                rowLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                // Iterate over each letter in the row
                for (col in letterArray[row].indices) {
                    // Create a Button for the letter
                    val button = Button(requireContext())
                    button.text = letterArray[row][col].toString()
                    button.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    button.setOnClickListener {
                        // Call the buttonClicked function with the coordinates of the clicked button
                        buttonClicked(row, col)
                    }

                    // Add the Button to the rowLayout
                    rowLayout.addView(button)
                }

                Log.d("MainPanel","A row of buttons added")
                binding.buttonContainer.addView(rowLayout)
            }
        }
    }

    private fun buttonClicked(row: Int, col: Int) {
        activityListener?.clickButton(row,col)
        updateUI()
    }

    fun updateUI(){
        inflateButtonArray()
        updateEnabledButtons()
        updateCurrWord()
    }

    fun updateEnabledButtons(){
        val enabledButtons:MutableList<Pair<Int, Int>> = activityListener?.getEnabledButtons() ?: mutableListOf()
        for (row in 0 until SIZE_OF_GAME) {
            // Iterate over each letter in the row
            for (col in 0 until SIZE_OF_GAME) {
                // Find the button at the current coordinates (row, col)
                val button = findButton(row, col)

                // Enable or disable the button based on its coordinates in the enabledButtons list
                val isEnabled = Pair(row, col) in enabledButtons
                button.isEnabled = isEnabled
            }
        }
    }

    fun updateCurrWord(){
        binding.currentWordText.text= activityListener?.getCurrWord() ?: "non"
    }

    private fun findButton(row: Int, col: Int): Button {
        // Calculate the index of the button in the buttonContainer
        val index = row * SIZE_OF_GAME + col

        // Get the button at the calculated index
        val rowLayout = binding.buttonContainer.getChildAt(row) as LinearLayout
        return rowLayout.getChildAt(col) as Button
    }


}