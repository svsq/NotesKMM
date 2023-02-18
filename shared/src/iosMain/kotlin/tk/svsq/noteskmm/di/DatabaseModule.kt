package tk.svsq.noteskmm.di

import tk.svsq.noteskmm.data.local.DatabaseDriverFactory
import tk.svsq.noteskmm.data.note.SqlDelightNoteDataSource
import tk.svsq.noteskmm.database.NoteDatabase
import tk.svsq.noteskmm.domain.note.NoteDataSource

class DatabaseModule {

    private val factory by lazy { DatabaseDriverFactory() }

    val noteDataSource: NoteDataSource by lazy {
        SqlDelightNoteDataSource(NoteDatabase(factory.createDriver()))
    }
}