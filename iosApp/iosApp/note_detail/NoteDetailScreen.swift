//
//  NoteDetailScreen.swift
//  iosApp
//
//  Created by Slavic Bilokur on 06.01.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteDetailScreen: View {
    
    private var noteDataSource: NoteDataSource
    private var noteId: Int64? = nil
    
    @StateObject private var viewModel = NoteDetailViewModel(noteDataSource: nil)
    
    @Environment(\.presentationMode) var presentation
    
    init(noteDataSource: NoteDataSource, noteId: Int64? = nil) {
        self.noteDataSource = noteDataSource
        self.noteId = noteId
        UITextView.appearance().backgroundColor = .clear
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            TextField("Enter a title...", text: $viewModel.noteTitle)
                .font(.title)
            if #available(iOS 16.0, *) {
                TextEditor(text: $viewModel.noteContent)
                    .scrollContentBackground(.hidden)
                    .background(Color(hex: viewModel.noteColor))
            } else {
                TextEditor(text: $viewModel.noteContent)
                    .background(Color(hex: viewModel.noteColor))
            }
            Spacer()
        }.toolbar(content: {
            Button(action: {
                viewModel.saveNote {
                    self.presentation.wrappedValue.dismiss()
                }
            }) {
                Image(systemName: "checkmark")
            }
            })
            .padding()
            .background(Color(hex: viewModel.noteColor))
            .onAppear {
                viewModel.setParamsAndLoadNote(noteDataSource: noteDataSource, noteId: noteId)
            }
    }
}

struct NoteDetailScreen_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}
