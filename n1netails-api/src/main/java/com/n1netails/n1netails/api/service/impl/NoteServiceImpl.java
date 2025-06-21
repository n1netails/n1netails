package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.NoteNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.Note;
import com.n1netails.n1netails.api.model.entity.NoteEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.NotePageRequest;
import com.n1netails.n1netails.api.repository.NoteRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("noteService")
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TailRepository tailRepository;

    @Override
    public Note add(Note note) throws TailNotFoundException, UserNotFoundException {
        TailEntity tail = this.tailRepository.findById(note.getTailId())
                .orElseThrow(() -> new TailNotFoundException("Tail for the requested new note does note exist."));
        UsersEntity user = this.userRepository.findById(note.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User who requested to add new note does not exist."));

        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setTail(tail);
        noteEntity.setUser(user);
        noteEntity.setContent(note.getContent());
        noteEntity.setCreatedAt(Instant.now());
        noteEntity.setHuman(note.isHuman());
        noteEntity.setN1(noteEntity.isN1());
        noteEntity.setOrganization(tail.getOrganization());

        // save note
        noteEntity = this.noteRepository.save(noteEntity);
        note.setId(noteEntity.getId());
        return note;
    }

    @Override
    public Note getById(Long id) throws NoteNotFoundException {
        NoteEntity noteEntity = this.noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Requested not does not exist."));
        return setNote(noteEntity);
    }

    @Override
    public List<Note> getAllByTailId(Long tailId) {
        return this.noteRepository.findAllByTailIdOrderByCreatedAtDesc(tailId)
                .stream()
                .map(NoteServiceImpl::setNote)
                .collect(Collectors.toList());
    }

    @Override
    public List<Note> getLast9NotesByTailId(Long tailId) {
        return this.noteRepository.findTop9ByTailIdOrderByCreatedAtDesc(tailId)
                .stream()
                .map(NoteServiceImpl::setNote)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Note> getNotesByTailId(NotePageRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdAt").descending());
        Page<NoteEntity> noteEntitiesPage = this.noteRepository.findAllByTailId(request.getTailId(), pageable);
        return noteEntitiesPage.map(NoteServiceImpl::setNote);
    }

    private static Note setNote(NoteEntity noteEntity) {
        Note note = new Note();
        note.setId(noteEntity.getId());
        note.setTailId(noteEntity.getTail().getId());
        note.setOrganizationId(noteEntity.getOrganization().getId());
        note.setUserId(noteEntity.getUser().getId());
        note.setUsername(noteEntity.getUser().getUsername());
        note.setHuman(noteEntity.isHuman());
        note.setN1(noteEntity.isN1());
        note.setLlmProvider(noteEntity.getLlmProvider());
        note.setLlmModel(noteEntity.getLlmModel());
        note.setCreatedAt(noteEntity.getCreatedAt());
        note.setContent(noteEntity.getContent());
        return note;
    }
}
