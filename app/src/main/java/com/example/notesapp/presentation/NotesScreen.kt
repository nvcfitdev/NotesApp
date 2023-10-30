package com.example.notesapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notesapp.domain.model.Note
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun NotesScreen(
    navigator: DestinationsNavigator,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isLoading)
    val state = viewModel.state
    var text by remember { mutableStateOf(TextFieldValue()) }
    var openDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SwipeRefresh(state = swipeRefreshState, onRefresh = { viewModel.queryNotes() }) {
            NoteColumn(modifier = Modifier.fillMaxSize(), state.noteList)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Filled.AddCircleOutline, "") },
                    text = { Text("Add Note") },
                    onClick = { openDialog.value = true },
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }

        if (openDialog.value) {
            AlertDialog(
                modifier = Modifier
                    .requiredHeight(500.dp)
                    .fillMaxWidth(),
                containerColor = Color.LightGray,
                onDismissRequest = {
                    openDialog.value = false
                },
                confirmButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                            if (!text.text.isNullOrEmpty()) {
                                viewModel.onEvent(NotesEvent.CreateNoteEvent(text.text))
                            }
                            text = TextFieldValue()
                        }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                            text = TextFieldValue()
                        }) {
                        Text("No")
                    }
                },
                title = { Text("New Note", color = Color.Black, fontWeight = FontWeight.Medium) },
                text = {
                    BasicTextField(
                        value = text,
                        onValueChange = { newText ->
                            text = newText
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Handle the "Done" action here
                            }
                        ),
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.White)
                            .border(1.dp, color = Color.LightGray)
                            .padding(12.dp),
                    )
                },
            )
        }
    }
}

@Composable
fun NoteColumn(
    modifier: Modifier = Modifier,
    noteList: MutableList<Note>,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val openDialog = remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf(Note(0, "")) }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(noteList.size) { index ->
            val data = noteList[index]
            NoteItem(note = data, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    selectedNote = data
                    openDialog.value = true
                }
            )
            if (index < noteList.size) {
                Divider(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp))
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            modifier = Modifier
                .width(300.dp)
                .height(200.dp),
            containerColor = Color.LightGray,
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        viewModel.onEvent(NotesEvent.DeleteNoteEvent(selectedNote))
                        noteList.remove(selectedNote)
                        noteList.toMutableList()
                    }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { openDialog.value = false }) {
                    Text("No")
                }
            },
            title = { Text("Wait!", color = Color.Black, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Do you want to delete this Note?",
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )
            },
        )
    }
}

@Composable
fun NoteItem(
    note: Note,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = note.note,
            maxLines = 4,
            fontWeight = FontWeight.Normal,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )
    }
}
