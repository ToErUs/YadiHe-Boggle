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

}
class MainPanelFragment: Fragment() {
    private lateinit var binding: MainPanelBinding
    private var activityListener: MainPanelCommunicator? = null

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

                    // Add the Button to the rowLayout
                    rowLayout.addView(button)
                }

                Log.d("MainPanel","A row of buttons added")
                binding.buttonContainer.addView(rowLayout)
            }
        }
    }


}