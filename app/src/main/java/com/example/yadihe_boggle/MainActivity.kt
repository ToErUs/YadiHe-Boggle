package com.example.yadihe_boggle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(),MainPanelCommunicator,ScoreBarCommunicator,
    SensorEventListener {
    private val boggleViewModel: BoggleViewModel by viewModels()
    //val boggleViewModel = ViewModelProvider(this, ViewModelFactory(applicationContext)).get(BoggleViewModel::class.java)
    val mainPanelFragment=MainPanelFragment()
    val scoreBarFragment=ScoreBarFragment()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastAcceleration: Float = 0f
    private val shakeThreshold = 5f // Adjust as needed
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
        boggleViewModel.toastMessage.observe(this, Observer { message ->
            // Show toast when message is updated
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun getLetterArray(): Array<CharArray> {
        return boggleViewModel.letterArray
    }

    override fun clickButton(row: Int, col: Int) {
        boggleViewModel.hitButton(row,col)
    }

    override fun getEnabledButtons(): MutableList<Pair<Int, Int>> {
        return boggleViewModel.enabledButtons
    }

    override fun getCurrWord(): String {
        val w=boggleViewModel.getCurrentWord()
        Log.d("MainActivity","CurrWord: $w")
        return w
    }

    override fun submit() {
        if(!wordInDictionary(boggleViewModel.getCurrentWord())){
            boggleViewModel.submitWrongWord()
        }else{
            boggleViewModel.submitWord()
        }

    }

    override fun updateScore() {
        scoreBarFragment.updateScoreUI()
    }

    override fun clear() {
        boggleViewModel.clearCurrentWord()
    }

    override fun getCurrScore(): Int {
        return boggleViewModel.getCurrentScore()
    }

    override fun startNewGame() {
        boggleViewModel.resetGame()
        mainPanelFragment.updateUI()
        scoreBarFragment.updateScoreUI()
        Toast.makeText(this, "New game!", Toast.LENGTH_SHORT).show()
    }

    fun wordInDictionary(word:String):Boolean{
        try {
            val inputStream = resources.openRawResource(R.raw.words)
            val dictionaryContent = inputStream.bufferedReader().use { it.readText() }
            val wordList = dictionaryContent.split("\n")
            return word.lowercase() in wordList
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val acceleration = calculateAcceleration(it.values[0], it.values[1], it.values[2])
                val deltaAcceleration = acceleration - lastAcceleration


                if (deltaAcceleration > shakeThreshold&&lastAcceleration!=0f) {

                    startNewGame()
                }
                lastAcceleration = acceleration
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //
    }
    private fun calculateAcceleration(x: Float, y: Float, z: Float): Float {
        return Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }
}