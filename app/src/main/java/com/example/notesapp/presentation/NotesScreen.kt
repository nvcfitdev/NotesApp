package com.example.notesapp.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh(state = swipeRefreshState, onRefresh = { viewModel.queryNotes() }) {
            NoteColumn(modifier = Modifier.fillMaxSize(), state.noteList)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )
            AddNoteButton(newNote = text.text)
        }
    }
}

@Composable
fun NoteColumn(
    modifier: Modifier = Modifier,
    noteList: MutableList<Note>,
    viewModel: NotesViewModel = hiltViewModel()
) {
    var data by remember { mutableStateOf(noteList) }
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
fun AddNoteDialog() {
    Column {
        val openDialog = remember { mutableStateOf(false) }

        Button(onClick = {
            openDialog.value = true
        }) {
            Text("Click me")
        }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user clicks outside the dialog or on the back
                    // button. If you want to disable that functionality, simply use an empty
                    // onCloseRequest.
                    openDialog.value = false
                },
                title = {
                    Text(text = "Dialog Title")
                },
                text = {

                },
                confirmButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text("This is the Confirm Button")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text("This is the dismiss Button")
                    }
                }
            )
        }
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
            modifier = Modifier.padding(3.dp),
            maxLines = 4,
            fontWeight = FontWeight.Normal,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )
    }
}

@Composable
fun AddNoteButton(
    newNote: String = "",
    viewModel: NotesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        ExtendedFloatingActionButton(
            icon = { Icon(Icons.Filled.AddTask, "") },
            text = { Text("Add Note") },
            onClick = {
                viewModel.onEvent(NotesEvent.CreateNoteEvent(newNote))
            },
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .padding(16.dp)  // Optional padding
        )

        ExtendedFloatingActionButton(
            icon = { Icon(Icons.Filled.HorizontalRule, "") },
            text = { Text("Delete Note") },
            onClick = {
                viewModel.onEvent(NotesEvent.CreateNoteEvent(newNote))
            },
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .padding(16.dp)  // Optional padding
        )
    }
}
