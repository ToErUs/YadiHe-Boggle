package com.example.yadihe_boggle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

class MainActivity : AppCompatActivity(),MainPanelCommunicator,ScoreBarCommunicator {
    private val boggleViewModel: BoggleViewModel by viewModels()
    val mainPanelFragment=MainPanelFragment()
    val scoreBarFragment=ScoreBarFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container1, mainPanelFragment)
            .commit()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container2, scoreBarFragment)
            .commit()
    }

    override fun getLetterArray(): Array<CharArray> {
        return boggleViewModel.letterArray
    }

    override fun getCurrScore(): Int {
        return boggleViewModel.score
    }
}