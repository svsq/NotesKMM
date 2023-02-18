package tk.svsq.noteskmm.android.note_list

import tk.svsq.noteskmm.domain.note.Note

data class NoteListState(
    val notes: List<Note> = emptyList(),
    val searchText: String = "",
    val isSearchActive: Boolean = false
)