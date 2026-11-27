package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.request.NoteRequestDto;
import org.example.dto.response.NoteResponseDto;
import org.example.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(
            @Valid @RequestBody NoteRequestDto request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(
                        request, getUserId(authentication)));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotes(
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.getAllNotes(getUserId(authentication)));
    }

    @GetMapping("/active")
    public ResponseEntity<List<NoteResponseDto>> getActiveNotes(
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.getActiveNotes(getUserId(authentication)));
    }

    @GetMapping("/archived")
    public ResponseEntity<List<NoteResponseDto>> getArchivedNotes(
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.getArchivedNotes(getUserId(authentication)));
    }

    @GetMapping("/trashed")
    public ResponseEntity<List<NoteResponseDto>> getTrashedNotes(
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.getTrashedNotes(getUserId(authentication)));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> getNoteById(
            @PathVariable Long noteId,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.getNoteById(noteId, getUserId(authentication)));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> updateNote(
            @PathVariable Long noteId,
            @Valid @RequestBody NoteRequestDto request,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.updateNote(
                        noteId, request, getUserId(authentication)));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long noteId,
            Authentication authentication) {

        noteService.deleteNote(noteId, getUserId(authentication));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{noteId}/archive")
    public ResponseEntity<NoteResponseDto> archiveNote(
            @PathVariable Long noteId,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.archiveNote(noteId, getUserId(authentication)));
    }

    @PatchMapping("/{noteId}/unarchive")
    public ResponseEntity<NoteResponseDto> unarchiveNote(
            @PathVariable Long noteId,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.unarchiveNote(noteId, getUserId(authentication)));
    }

    @PatchMapping("/{noteId}/pin")
    public ResponseEntity<NoteResponseDto> pinNote(
            @PathVariable Long noteId,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.pinNote(noteId, getUserId(authentication)));
    }

    @PatchMapping("/{noteId}/unpin")
    public ResponseEntity<NoteResponseDto> unpinNote(
            @PathVariable Long noteId,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.unpinNote(noteId, getUserId(authentication)));
    }

    @PatchMapping("/{noteId}/trash")
    public ResponseEntity<NoteResponseDto> trashNote(
            @PathVariable Long noteId,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.trashNote(noteId, getUserId(authentication)));
    }

    @PatchMapping("/{noteId}/restore")
    public ResponseEntity<NoteResponseDto> restoreNote(
            @PathVariable Long noteId,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.restoreNote(noteId, getUserId(authentication)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponseDto>> searchNotes(
            @RequestParam String keyword,
            Authentication authentication) {

        return ResponseEntity.ok(
                noteService.searchNotes(
                        getUserId(authentication), keyword));
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}