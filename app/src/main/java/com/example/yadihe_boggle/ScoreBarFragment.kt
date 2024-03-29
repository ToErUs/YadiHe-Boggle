package com.example.yadihe_boggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yadihe_boggle.databinding.ScoreBarBinding

interface ScoreBarCommunicator {
    fun getCurrScore(): Int

}
class ScoreBarFragment:Fragment() {
    private lateinit var binding: ScoreBarBinding
    private var activityListener: ScoreBarCommunicator? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            ScoreBarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateScoreUI()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ScoreBarCommunicator) {
            activityListener = context
        } else {
            throw RuntimeException("$context must implement DataProvider")
        }
    }

    fun updateScoreUI(){
        binding.scoreText.text="Score: "+ activityListener?.getCurrScore().toString()
    }
}