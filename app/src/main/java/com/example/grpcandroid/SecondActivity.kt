package com.example.grpcandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.grpcandroid.ui.theme.GRpcAndroidTheme

class SecondActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GRpcAndroidTheme {
                Text("SecondActivity")
            }
        }
    }
}