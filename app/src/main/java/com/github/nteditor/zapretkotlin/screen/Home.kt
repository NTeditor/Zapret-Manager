package com.github.nteditor.zapretkotlin.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


@Preview
@Composable
fun Home() {

    var tvStatus by remember { mutableStateOf("") }
    val tvPid by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp, horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .fillMaxWidth()
                ,

        ) {
            Column(
                modifier = Modifier
                    .padding(start = 25.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = tvStatus, fontSize = 24.sp)
                Text(text = tvPid, fontSize = 18.sp)
            }
        }
        Row {
            val context = LocalContext.current
            Button(onClick = {
                Toast.makeText(context, "test1", Toast.LENGTH_SHORT).show()
                tvStatus = "test1"
            }) {
                Text( text = "test1" )
            }
            Button(onClick = {
                Toast.makeText(context, "test2", Toast.LENGTH_SHORT).show()
                tvStatus = "test2"
            }) {
                Text( text = "test2" )
            }
        }
    }
}

