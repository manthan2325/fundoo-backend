package org.example.service.impl;

import org.example.dto.request.NoteRequestDto;
import org.example.dto.response.NoteResponseDto;
import org.example.entity.NoteEntity;
import org.example.entity.UserEntity;
import org.example.exception.NoteNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.repository.NoteRepository;
import org.example.repository.UserRepository;
import org.example.service.NoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteServiceImpl(NoteRepository noteRepository,
                           UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @Override
    public NoteResponseDto createNote(NoteRequestDto request, Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        NoteEntity note = new NoteEntity();
        note.setTitle(request.title());
        note.setDescription(request.description());
        note.setUser(user);

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getAllNotes(Long userId) {

        return noteRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getActiveNotes(Long userId) {

        return noteRepository
                .findByUserIdAndArchivedFalseAndTrashedFalse(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getArchivedNotes(Long userId) {

        return noteRepository
                .findByUserIdAndArchivedTrueAndTrashedFalse(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> getTrashedNotes(Long userId) {

        return noteRepository.findByUserIdAndTrashedTrue(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponseDto getNoteById(Long noteId, Long userId) {

        return mapToResponse(getOwnedNote(noteId, userId));
    }

    @Override
    public NoteResponseDto updateNote(
            Long noteId, NoteRequestDto request, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);

        note.setTitle(request.title());
        note.setDescription(request.description());

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    public void deleteNote(Long noteId, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);
        noteRepository.delete(note);
    }

    @Override
    public NoteResponseDto archiveNote(Long noteId, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);
        note.setArchived(true);

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponseDto unarchiveNote(Long noteId, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);
        note.setArchived(false);

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponseDto pinNote(Long noteId, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);
        note.setPinned(true);

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponseDto unpinNote(Long noteId, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);
        note.setPinned(false);

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponseDto trashNote(Long noteId, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);
        note.setTrashed(true);

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    public NoteResponseDto restoreNote(Long noteId, Long userId) {

        NoteEntity note = getOwnedNote(noteId, userId);
        note.setTrashed(false);

        return mapToResponse(noteRepository.save(note));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponseDto> searchNotes(Long userId, String keyword) {

        return noteRepository
                .findByUserIdAndTitleContainingIgnoreCase(userId, keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NoteEntity getOwnedNote(Long noteId, Long userId) {

        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new NoteNotFoundException("Note not found"));

        if (!note.getUser().getId().equals(userId)) {
            throw new NoteNotFoundException("Note not found");
        }

        return note;
    }

    private NoteResponseDto mapToResponse(NoteEntity note) {

        return new NoteResponseDto(
                note.getId(),
                note.getTitle(),
                note.getDescription(),
                note.isPinned(),
                note.isArchived(),
                note.isTrashed()
        );
    }
}