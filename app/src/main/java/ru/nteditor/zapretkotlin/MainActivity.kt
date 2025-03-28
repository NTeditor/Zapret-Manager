package ru.nteditor.zapretkotlin


import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.os.Environment
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.net.toUri

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

        val textTester1 = findViewById<TextView>(R.id.tvLogView)
        val btnZapretStop = findViewById<Button>(R.id.btnZapretStop)
        val btnZapretStart = findViewById<Button>(R.id.btnZapretStart)
        val zapretStatusCard = findViewById<TextView>(R.id.tvZapretStatus)
        val zapretPidText = findViewById<TextView>(R.id.tvZapretPid)
        val downloadButton = findViewById<TextView>(R.id.btnDownload)

        fun checkZapretFile(): Boolean {
            fun checkZapretFileSub(): Boolean {
                return File("/system/bin/zapret").exists()
            }
            if (!checkZapretFileSub()) {
                downloadButton.visibility = View.VISIBLE
            }

            return checkZapretFileSub()
        }


        fun zapretCheckStatus() {
            val zapretStatusCMD =
                listOf("su", "-c", "pidof", "nfqws").runCommand(File("/system/bin"))
            if (zapretStatusCMD == "") {
                val zapretStatus = getString(R.string.zapret_status_disable)
                zapretStatusCard.text = zapretStatus
                zapretPidText.text = ""

            } else {
                val zapretStatus = getString(R.string.zapret_status_enable)
                val zapretStatusPid = getString(R.string.zapret_status_pid)
                zapretStatusCard.text = zapretStatus
                zapretPidText.text = "$zapretStatusPid $zapretStatusCMD"
            }


        }

        fun checkSUFile(): Boolean {
            return File("/system/bin/su").exists() || File("/system/xbin/su").exists() || File("/system/sbin/su").exists()
        }

        if (checkSUFile()) {
            if (checkZapretFile()) {
                zapretCheckStatus()
            } else {
                zapretStatusCard.text = getString(R.string.zapret_not_found)
            }
        } else {
            zapretStatusCard.text = getString(R.string.su_not_found)
        }



        fun zapretInstaller() {
            if(!checkSUFile()) {
                return
            }
            val zapretDownloadTitle = getString(R.string.zapret_download_title)
            val zapretDownloadMessage = getString(R.string.zapret_download_message)
            val zapretDownloadPositive = getString(R.string.zapret_download_positive)
            val zapretDownloadNatural = getString(R.string.zapret_download_neutral)
            val zapretDownloadNegative = getString(R.string.zapret_download_negative)
            val zapretDownloadFileName = getString(R.string.zapret_download_file_name)

            val zapretDownloadPositiveUrl = "https://github.com/ImMALWARE/zapret-magisk/releases/latest/download/zapret_module.zip"
            val zapretDownloadNegativeUrl = "https://github.com/BAGAbball/zapret-magisk/releases/latest/download/zapret_module.zip"

            fun downloadAlert(title: String, message: String, positiveButton: String, neutralButton: String, negativeButton: String, fileUrlPositive: String, fileUrlNegative: String, fileName: String) {

                fun downloadStarter(fileUrl: String, fileName: String) {
                    val request = DownloadManager.Request(fileUrl.toUri())
                        .setTitle(fileName)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        .setAllowedOverMetered(true)

                    val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)

                }

                fun installZip() {
                    val outputCMD = listOf("su", "-c", "magisk", "--install-module", "/sdcard/download/zapret_magisk.zip").runCommand(File("/sdcard"))
                    textTester1.text = outputCMD
                }

                MaterialAlertDialogBuilder(this, R.style.Alert_style)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(positiveButton)
                    { dialog, _ -> downloadStarter(fileUrlPositive, fileName)
                        installZip()
                        dialog.dismiss() }
                    .setNeutralButton(neutralButton)
                    { dialog, _ -> dialog.dismiss() }
                    .setNegativeButton(negativeButton)
                    { dialog, _ -> downloadStarter(fileUrlNegative, fileName)
                        installZip()
                        dialog.dismiss() }
                    .show()
            }

            downloadAlert(zapretDownloadTitle, zapretDownloadMessage, zapretDownloadPositive, zapretDownloadNatural, zapretDownloadNegative, zapretDownloadPositiveUrl, zapretDownloadNegativeUrl, zapretDownloadFileName)
            if (checkZapretFile()) {
                zapretCheckStatus()
            } else {
                zapretStatusCard.text = getString(R.string.zapret_not_found)
            }
        }



        btnZapretStart.setOnClickListener {
            if (checkSUFile()) {
                if (checkZapretFile()) {
                    val outputCMD =
                        listOf("su", "-c", "zapret", "start").runCommand(File("/system"))
                    textTester1.text = outputCMD
                    zapretCheckStatus()
                } else {
                    zapretStatusCard.text = getString(R.string.zapret_not_found)

                }
            } else {
                zapretStatusCard.text = getString(R.string.su_not_found)

            }


        }

        btnZapretStop.setOnClickListener {
            if (checkSUFile()) {
                if (checkZapretFile()) {
                    val outputCMD = listOf("su", "-c", "zapret", "stop").runCommand(File("/system"))
                    textTester1.text = outputCMD
                    zapretCheckStatus()
                } else {
                    zapretStatusCard.text = getString(R.string.zapret_not_found)
                }
            } else {
                zapretStatusCard.text = getString(R.string.su_not_found)
            }
        }

        downloadButton.setOnClickListener {
            zapretInstaller()
        }


    }


}