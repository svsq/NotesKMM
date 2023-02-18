package tk.svsq.noteskmm.android.note_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tk.svsq.noteskmm.domain.note.Note
import tk.svsq.noteskmm.domain.note.NoteDataSource
import tk.svsq.noteskmm.domain.time.DateTimeUtil
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteDataSource: NoteDataSource,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val NOTE_ID = "noteId"
        private const val NOTE_TITLE = "noteTitle"
        private const val IS_NOTE_TITLE_FOCUSED = "isNoteTitleFocused"
        private const val NOTE_CONTENT = "noteContent"
        private const val IS_NOTE_CONTENT_FOCUSED = "isNoteContentFocused"
        private const val NOTE_COLOR = "noteColor"
    }

    private val noteTitle = savedStateHandle.getStateFlow(NOTE_TITLE, "")
    private val isNoteTitleFocused = savedStateHandle.getStateFlow(IS_NOTE_TITLE_FOCUSED, false)
    private val noteContent = savedStateHandle.getStateFlow(NOTE_CONTENT, "")
    private val isNoteContentFocused = savedStateHandle.getStateFlow(IS_NOTE_CONTENT_FOCUSED, false)
    private val noteColor = savedStateHandle.getStateFlow(NOTE_COLOR, Note.generateRandomColor())

    val state = combine(
        noteTitle,
        isNoteTitleFocused,
        noteContent,
        isNoteContentFocused,
        noteColor
    ) { title, isTitleFocused, content, isContentFocused, color ->
        NoteDetailState(
            noteTitle = title,
            isNoteTitleHintVisible = title.isEmpty() && !isTitleFocused,
            noteContent = content,
            isNoteContentHintVisible = content.isEmpty() && !isContentFocused,
            noteColor = color
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteDetailState())

    private val _hasNoteBeenSaved = MutableStateFlow(false)
    val hasNoteBeenSaved = _hasNoteBeenSaved.asStateFlow()

    private var existingNoteId: Long? = null

    init {
        savedStateHandle.get<Long>(NOTE_ID)?.let { existingNoteId ->
            if(existingNoteId < 0) {
                return@let
            }
            this.existingNoteId = existingNoteId
            viewModelScope.launch {
                noteDataSource.getNoteById(existingNoteId)?.let { note ->
                    savedStateHandle[NOTE_TITLE] = note.title
                    savedStateHandle[NOTE_CONTENT] = note.content
                    savedStateHandle[NOTE_COLOR] = note.colorHex
                }
            }
        }
    }

    fun onNoteTitleChanged(text: String) {
        savedStateHandle[NOTE_TITLE] = text
    }

    fun onNoteContentChanged(text: String) {
        savedStateHandle[NOTE_CONTENT] = text
    }

    fun onNoteTitleFocusChanged(isFocused: Boolean) {
        savedStateHandle[IS_NOTE_TITLE_FOCUSED] = isFocused
    }

    fun onNoteContentFocusChanged(isFocused: Boolean) {
        savedStateHandle[IS_NOTE_CONTENT_FOCUSED] = isFocused
    }

    fun saveNote() {
        viewModelScope.launch {
            noteDataSource.insertNote(
                Note(
                    id = existingNoteId,
                    title = noteTitle.value,
                    content = noteContent.value,
                    colorHex = noteColor.value,
                    created = DateTimeUtil.now()
                )
            )
            _hasNoteBeenSaved.value = true
        }
    }
}