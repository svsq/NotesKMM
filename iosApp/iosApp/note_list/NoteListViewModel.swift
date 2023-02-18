//
//  NoteListViewModel.swift
//  iosApp
//
//  Created by Slavic Bilokur on 05.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

extension NoteListScreen {
    @MainActor class NoteListViewModel: ObservableObject {
        private var noteDataSource: NoteDataSource? = nil
        
        private let searchNotes = SearchNotes()
        
        private var notes = [Note]() // empty list - equal to listOf<Note>()
        @Published private(set) var filteredNotes = [Note]()
        @Published var searchText = "" {
            didSet {
                self.filteredNotes = searchNotes.execute(notes: self.notes, query: searchText)
            }
        }
        @Published private(set) var isSearchActive = false
        
        init(noteDataSource: NoteDataSource? = nil) {
            self.noteDataSource = noteDataSource
        }
        
        func loadNotes() {
            noteDataSource?.getAllNotes(completionHandler: { notes, error in
                self.notes = notes ?? [] // assign empty list if nil
                self.filteredNotes = self.notes
            })
        }
        
        func deleteNoteById(id: Int64?) { // Int64 is eq to long in Kotlin
            if id != nil {
                noteDataSource?.deleteNoteById(id: id!, completionHandler: { error in
                    self.loadNotes()
                })
            }
        }
        
        func toggleIsSearchActive() {
            isSearchActive = !isSearchActive
            if !isSearchActive {
                searchText = ""
            }
        }
 
        func setNoteDataSourcce(noteDataSource: NoteDataSource) {
            self.noteDataSource = noteDataSource
        }
    }
}
