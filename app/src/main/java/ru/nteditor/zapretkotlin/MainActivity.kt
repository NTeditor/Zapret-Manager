package ru.nteditor.zapretkotlin


import android.os.Bundle
import android.view.View
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
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
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

        val btnDownload = findViewById<TextView>(R.id.btnDownload)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvStatusSub = findViewById<TextView>(R.id.tvStatusSub)


        fun checkSUFile(): Boolean {
            fun checkFile(): Boolean {
                return File("/system/bin/su").exists() || File("/system/xbin/su").exists() || File("/system/sbin/su").exists() || File(
                    "/bin/su"
                ).exists() || File("/xbin/su").exists() || File("/sbin/su").exists()
            }
            if (!checkFile()) {
                return false
            }
            val checkPermission = listOf("su", "-s", "ls").runCommand(File("/system"))
            if (checkPermission != "") {
                return true
            } else {
                return false
            }
        }

        fun checkZapretFile(): Boolean {
            return File("/system/bin/zapret").exists()
        }

        fun updateStatus() {
            if (checkZapretFile()) {
                val zapretStatusCMD =
                    listOf("su", "-c", "pidof", "nfqws").runCommand(File("/system/bin"))
                if (zapretStatusCMD == "") {
                    tvStatus.text = getString(R.string.zapret_status_disable)
                    tvStatusSub.text = ""

                } else {
                    tvStatus.text = getString(R.string.zapret_status_enable)
                    tvStatusSub.text = "pid: $zapretStatusCMD"
                }
            } else {
                tvStatus.text = getString(R.string.zapret_not_found)
            }
        }

        if (!checkSUFile()) {
            btnDownload.visibility = View.GONE
            btnStart.visibility = View.GONE
            btnStop.visibility = View.GONE

            tvStatus.text = getString(R.string.su_not_found)
        } else {
            updateStatus()
        }


        btnStart.setOnClickListener {
            val outputCMD =
                listOf("su", "-c", "zapret", "start").runCommand(File("/system"))
            updateStatus()
        }

        btnStop.setOnClickListener {
            val outputCMD = listOf("su", "-c", "zapret", "stop").runCommand(File("/system"))
        updateStatus()
        }
    }
}