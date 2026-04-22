package com.loki.prime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.loki.prime.ui.ChatScreen

class MainActivity : ComponentActivity() {

    private val database by lazy { LokiDatabase.getDatabase(this) }
    
    private val viewModel: LokiViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LokiViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return LokiViewModel(database) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatScreen(viewModel = viewModel)
        }
    }
}
