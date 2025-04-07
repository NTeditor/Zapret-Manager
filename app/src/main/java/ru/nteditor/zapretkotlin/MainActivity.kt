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

class MainActivity : AppCompatActivity() {

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



        fun updateStatus() {
            if (IsShell().isZapret()) {
                val zapretStatusCMD = Shell(listOf("su", "-c", "pidof", "nfqws")).start()
                if (zapretStatusCMD == "") {
                    btnStop.visibility = View.GONE
                    btnStart.visibility = View.VISIBLE
                    tvStatus.text = getString(R.string.zapret_status_disable)
                    tvStatusPid.visibility = View.INVISIBLE
                    tvStatusNumber.text = ""

                } else {
                    btnStart.visibility = View.GONE
                    btnStop.visibility = View.VISIBLE
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

        if (!IsShell().isRoot()) {
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
                Shell(listOf("su", "-c", "zapret", "start")).start().toString())
            updateStatus()
        }

        btnStop.setOnClickListener {
            alert(getString(R.string.zapret_alert_stop),
                Shell(listOf("su", "-c", "zapret", "stop")).start().toString())
            updateStatus()
        }



        btnDownload.setOnClickListener {

        }
    }
}