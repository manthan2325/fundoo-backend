package org.example.repository;

import org.example.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findByUserId(Long userId);

    List<NoteEntity> findByUserIdAndArchivedFalseAndTrashedFalse(Long userId);

    List<NoteEntity> findByUserIdAndTitleContainingIgnoreCase(
            Long userId, String keyword);

    List<NoteEntity> findByUserIdAndArchivedTrueAndTrashedFalse(Long userId);

    List<NoteEntity> findByUserIdAndTrashedTrue(Long userId);
}