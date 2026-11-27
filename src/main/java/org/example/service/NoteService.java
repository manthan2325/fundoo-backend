package org.example.service;

import org.example.dto.request.NoteRequestDto;
import org.example.dto.response.NoteResponseDto;

import java.util.List;

public interface NoteService {

    NoteResponseDto createNote(NoteRequestDto request, Long userId);

    List<NoteResponseDto> getAllNotes(Long userId);

    List<NoteResponseDto> getActiveNotes(Long userId);

    List<NoteResponseDto> getArchivedNotes(Long userId);

    List<NoteResponseDto> getTrashedNotes(Long userId);

    NoteResponseDto getNoteById(Long noteId, Long userId);

    NoteResponseDto updateNote(Long noteId, NoteRequestDto request, Long userId);

    void deleteNote(Long noteId, Long userId);

    NoteResponseDto archiveNote(Long noteId, Long userId);

    NoteResponseDto unarchiveNote(Long noteId, Long userId);

    NoteResponseDto pinNote(Long noteId, Long userId);

    NoteResponseDto unpinNote(Long noteId, Long userId);

    NoteResponseDto trashNote(Long noteId, Long userId);

    NoteResponseDto restoreNote(Long noteId, Long userId);

    List<NoteResponseDto> searchNotes(Long userId, String keyword);
}