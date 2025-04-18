package com.github.nteditor.zapretkotlin.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.nteditor.zapretkotlin.IsShell
import com.github.nteditor.zapretkotlin.R
import com.github.nteditor.zapretkotlin.Shell



@Preview
@Composable
fun Home() {

    var tvStatus by remember { mutableStateOf("") }
    var btnText by remember { mutableStateOf("") }
    var tvPid by remember { mutableStateOf("") }

    /**
     * Получение текста из strings.xml
     *
     * zapretStatusText для верхнего поля Card,
     * zapretBtnText для кнопки Запуска/Остановки,
     * zapretBtnToast для toast при нажатии на кнопку.
     */
    val zapretStatusText = listOf<String>(
        stringResource(R.string.zapret_disable),
        stringResource(R.string.zapret_enable),
        stringResource(R.string.zapret_not_found),
        stringResource(R.string.not_root))

    val zapretBtnText = listOf<String>(
        stringResource(R.string.btn_zapret_enable),
        stringResource(R.string.btn_zapret_disable),
        stringResource(R.string.btn_zapret_download)
    )

    val zapretBtnToast = listOf<String>(
        stringResource(R.string.btn_zapret_toast_stop),
        stringResource(R.string.btn_zapret_toast_start),
        stringResource(R.string.btn_zapret_toast_download)
    )

    val context = LocalContext.current

    /**
     * Обновляет статус zapret и обновляет тектовые поля.
     *
     * Причина почему не используется stringResource() напремую это:
     * 1. Метод придется сделать @Composable, и его будет не возможно вызвать при нажатии на кнопку.
     * 2. Меньше if (хотя это больше следствие первого)
     */
    fun updateStatus() {
        if(IsShell().isRoot()) {
            val zapretStatus = IsShell().zapretStatus()
            tvStatus = zapretStatusText[zapretStatus]
            tvPid = IsShell().getZapretPid()
            btnText = zapretBtnText[zapretStatus]
        } else {
            tvStatus = zapretStatusText[3]
            tvPid = ""
        }
    }

    updateStatus()

    /**
     * Останавливает zapret
     *
     * @return Результат выполнения команды
     */
    fun zapretStop(): String {
        Toast.makeText(context, zapretBtnToast[0], Toast.LENGTH_SHORT).show()
        return Shell(listOf("su", "-c", "zapret", "stop")).start().also {
            updateStatus()
        }
    }

    /**
     * Запускает zapret
     *
     * @return Результат выполнения команды
     */
    fun zapretStart(): String {
        Toast.makeText(context, zapretBtnToast[1], Toast.LENGTH_SHORT).show()
        return Shell(listOf("su", "-c", "zapret", "start")).start().also {
            updateStatus()
        }
    }


    /**
     * Заглушка для загрузки zapret
     */
    fun zapretDownload() {
        Toast.makeText(context, zapretBtnToast[2], Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp, horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(0.15f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = tvStatus, fontSize = 18.sp)
                Text(text = tvPid, fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row {
            if (IsShell().isRoot()) {
                Button(onClick = {
                    val zapretStatus = IsShell().zapretStatus()
                    when (zapretStatus) {
                        0 -> zapretStart()
                        1 -> zapretStop()
                        2 -> zapretDownload()
                    }
                }) {
                    Text(text = btnText)
                }
            }
        }
    }
}



