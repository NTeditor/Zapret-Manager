package ru.nteditor.zapretkotlin


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {


    private fun List<String>.runCommand(workingDir: File): String? {
        try {
            val proc = ProcessBuilder(this)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

//            proc.waitFor(60, TimeUnit.MINUTES)
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
        val tvStatusPid = findViewById<TextView>(R.id.tvStatusPid)
        val tvStatusNumber = findViewById<TextView>(R.id.tvStatusNumber)



        fun checkSUFile(): Boolean {
            fun checkFile(): Boolean {
                return File("/system/bin/su").exists() ||
                        File("/system/xbin/su").exists() ||
                        File("/system/sbin/su").exists() ||
                        File("/bin/su").exists() ||
                        File("/xbin/su").exists() ||
                        File("/sbin/su").exists()
            }
            if (!checkFile()) {
                return false
            }
            val checkPermission = listOf("su", "-s", "ls").runCommand(File("/system"))
            return checkPermission != ""
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
                    tvStatusPid.visibility = View.INVISIBLE
                    tvStatusNumber.text = ""

                } else {
                    tvStatus.text = getString(R.string.zapret_status_enable)
                    tvStatusPid.visibility = View.VISIBLE
                    tvStatusNumber.text = zapretStatusCMD
                }
            } else {
                //btnDownload.visibility = View.VISIBLE
                btnStart.visibility = View.GONE
                btnStop.visibility = View.GONE
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

        fun alert(title: String, message: String) {
            MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.alert_close)) {
                    dialog, _ -> dialog.dismiss()
                }
                .show()


        }

        btnStart.setOnClickListener {
            alert(getString(R.string.zapret_alert_start),
                listOf("su", "-c", "zapret", "start")
                    .runCommand(File("/system")).toString())
            updateStatus()
        }

        btnStop.setOnClickListener {
            alert(getString(R.string.zapret_alert_stop),
                listOf("su", "-c", "zapret", "stop")
                    .runCommand(File("/system")).toString())
            updateStatus()
        }



        btnDownload.setOnClickListener {

        }
    }
}