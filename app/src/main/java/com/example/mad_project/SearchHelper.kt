package com.example.mad_project

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp

/**
 * A helper class to initialize a Jetpack Compose SearchBar within a traditional Android View.
 * This bridges the gap between Java Fragments and Kotlin Compose UI.
 */
object SearchHelper {
    
    @JvmStatic
    fun initComposeSearchBar(composeView: ComposeView, onSearch: (String) -> Unit) {
        composeView.setContent {
            // Use Material3 Theme for modern look and feel
            MaterialTheme {
                var text by remember { mutableStateOf("") }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 6.dp,
                    color = Color.White
                ) {
                    TextField(
                        value = text,
                        onValueChange = {
                            text = it
                            onSearch(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search products...", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            if (text.isNotEmpty()) {
                                IconButton(onClick = {
                                    text = ""
                                    onSearch("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        singleLine = true
                    )
                }
            }
        }
    }
}
