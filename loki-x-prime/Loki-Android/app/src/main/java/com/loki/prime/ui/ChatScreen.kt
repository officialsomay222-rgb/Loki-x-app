package com.loki.prime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.prime.LokiViewModel
import com.loki.prime.MessageEntity
// Markdown string rendering using Markwon is possible but standard Text is used here as a placeholder for simplicity in this file

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: LokiViewModel) {
    val messages by viewModel.chatHistory.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    // Colors matching Loki-X-Prime
    val bgDark = Color(0xFF0A0A0A)
    val cyanAccent = Color(0xFF00F2FF)
    val bubbleDark = Color(0xFF151821)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(top = 24.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOKI ",
                color = cyanAccent,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 18.sp
            )
            Text(
                text = "PRIME",
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 18.sp
            )
        }

        // Chat History Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(msg)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = cyanAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "THINKING...",
                            color = cyanAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
        
        // Chat Input UI (Replicating ChatInput.tsx design)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF000000).copy(alpha = 0.8f))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(15.dp, RoundedCornerShape(24.dp))
                    .background(bubbleDark, RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), cyanAccent.copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plus Icon
                IconButton(onClick = { /* Attachment handling */ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.Gray)
                }

                // Input Field
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text("Send a message...", color = Color.Gray, fontSize = 14.sp)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textState.text.isNotBlank() && !isGenerating) {
                                viewModel.sendMessage(textState.text)
                                textState = TextFieldValue("")
                            }
                        }
                    )
                )

                // Send or Mic button
                if (textState.text.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(cyanAccent)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.sendMessage(textState.text)
                                textState = TextFieldValue("")
                            },
                            enabled = !isGenerating
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    IconButton(onClick = { /* Voice input */ }) {
                        Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: MessageEntity) {
    val isUser = message.role == "user"
    val cyanAccent = Color(0xFF00F2FF)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Label above bubble for Loki Prime
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            ) {
                Text(
                    text = "LOKI PRIME",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    color = if (isUser) Color(0xFF1E293B) else Color(0xFF151821),
                    shape = RoundedCornerShape(
                        topStart = 16.dp, 
                        topEnd = 16.dp, 
                        bottomStart = if (isUser) 16.dp else 4.dp, 
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) Color.Transparent else Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(
                        topStart = 16.dp, 
                        topEnd = 16.dp, 
                        bottomStart = if (isUser) 16.dp else 4.dp, 
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}
