package ru.nteditor.zapretkotlin


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {



    private fun List<String>.runCommand(workingDir: File): String? {
        try {
            val proc = ProcessBuilder(this)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            proc.waitFor(60, TimeUnit.MINUTES)
            return proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun checkZapretFile(): Boolean {
        return File("/system/bin/zapret").exists()
    }







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textTester1 = findViewById<TextView>(R.id.textTester1)
        val zapretButtonStop = findViewById<Button>(R.id.zapretButtonStop)
        val zapretButtonStart = findViewById<Button>(R.id.zapretButtonStart)
        val zapretStatusCard = findViewById<TextView>(R.id.zapretStatusCard)



        fun zapretCheckStatus() {
            if (checkZapretFile()) {
                val zapretStatusCMD = listOf("su", "-c", "pidof", "nfqws").runCommand(File("/system/bin"))
                if (zapretStatusCMD == "") {
                    val zapretStatus = getString(R.string.zapret_status_disable)
                    zapretStatusCard.text = "$zapretStatus $zapretStatusCMD"
                    textTester1.text = zapretStatusCMD

                } else {
                    val zapretStatus = getString(R.string.zapret_status_enable)
                    zapretStatusCard.text = "$zapretStatus $zapretStatusCMD"
                }
            } else {
                zapretStatusCard.text = getString(R.string.zapret_not_found)
            }

        }

        fun checkSUFile(): Boolean {
            return File("/system/bin/su").exists() || File("/system/xbin/su").exists() || File("/system/sbin/su").exists()
        }

        if (checkSUFile()) {
            zapretCheckStatus()
        } else {
            zapretStatusCard.text = getString(R.string.su_not_found)
        }


        zapretButtonStart.setOnClickListener {
            if (checkSUFile()) {
                val outputCMD = listOf("su", "-c", "zapret", "start").runCommand(File("/system"))
                textTester1.text = outputCMD
                zapretCheckStatus()
            } else {
                zapretStatusCard.text = getString(R.string.su_not_found)
            }



        }

        zapretButtonStop.setOnClickListener {
            if (checkSUFile()) {
                val outputCMD = listOf("su", "-c", "zapret", "stop").runCommand(File("/system"))
                textTester1.text = outputCMD
                zapretCheckStatus()
            } else {
                zapretStatusCard.text = getString(R.string.su_not_found)
            }

        }


    }
}