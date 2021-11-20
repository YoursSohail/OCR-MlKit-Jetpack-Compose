package com.sohail.jctextrecognition

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.rememberImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.sohail.jctextrecognition.ui.theme.JCTextRecognitionTheme
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    private var imageUri = mutableStateOf<Uri?>(null)
    private var textChanged = mutableStateOf("Scanned text will appear here..")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JCTextRecognitionTheme {
                Scaffold(
                    content = { MainScreen(this) }
                )
            }
        }
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val selectImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri.value = uri
        }

    private fun shareText(sharedText: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedText)
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    @Composable
    fun MainScreen(context: ComponentActivity) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(3f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        modifier = Modifier.align(Alignment.BottomStart),
                        onClick = {
                            selectImage.launch("image/*")
                        }) {
                        Icon(
                            Icons.Filled.Add,
                            "add",
                            tint = Color.Blue
                        )
                    }

                    if (imageUri.value != null) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberImagePainter(
                                data = imageUri.value
                            ),
                            contentDescription = "image"
                        )
                        IconButton(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            onClick = {
                                val image = InputImage.fromFilePath(context, imageUri.value!!)
                                recognizer.process(image)
                                    .addOnSuccessListener {
                                        textChanged.value = it.text
                                    }
                                    .addOnFailureListener {
                                        Log.e("TEXT_REC", it.message.toString())
                                    }
                            }) {
                            Icon(
                                Icons.Filled.Search,
                                "scan",
                                tint = Color.Blue
                            )
                        }
                    }

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        text = textChanged.value
                    )
                    IconButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onClick = {
                            shareText(textChanged.value)
                        }) {
                        Icon(
                            Icons.Filled.Share,
                            "share",
                            tint = Color.Blue
                        )
                    }
                }
            }
        }
    }
}

