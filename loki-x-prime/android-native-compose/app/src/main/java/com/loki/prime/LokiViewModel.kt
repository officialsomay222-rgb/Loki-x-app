package com.loki.prime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class LokiViewModel(
    private val database: LokiDatabase
) : ViewModel() {

    // Replaces Zustand App State
    val chatHistory: StateFlow<List<MessageEntity>> = database.messageDao()
        .getAllMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Initialize Gemini 
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro", 
        apiKey = "YOUR_API_KEY_HERE" // Will require configuration
    )

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        
        viewModelScope.launch {
            _isGenerating.value = true
            
            // 1. Save User Message
            val userMsg = MessageEntity(id = UUID.randomUUID().toString(), role = "user", content = userText)
            database.messageDao().insertMessage(userMsg)

            try {
                // 2. Call Gemini
                val response = generativeModel.generateContent(userText)
                
                // 3. Save AI Response
                response.text?.let { aiText ->
                    val aiMsg = MessageEntity(id = UUID.randomUUID().toString(), role = "model", content = aiText)
                    database.messageDao().insertMessage(aiMsg)
                }
            } catch (e: Exception) {
                // Handle Error
                val errorMsg = MessageEntity(id = UUID.randomUUID().toString(), role = "model", content = "Error: ${e.localizedMessage}")
                database.messageDao().insertMessage(errorMsg)
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
