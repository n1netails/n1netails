package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
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
import org.springframework.transaction.annotation.Transactional;

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
    public Note add(Note note) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException {

        if (note.isN1() && noteRepository.findFirstByTailIdAndN1IsTrueOrderByCreatedAtDesc(note.getTailId()).isPresent()) {
            throw new N1NoteAlreadyExistsException("Unable to add n1 note as one already exists for this tail.");
        }

        TailEntity tail = this.tailRepository.findById(note.getTailId())
                .orElseThrow(() -> new TailNotFoundException("Tail for the requested new note does note exist."));
        UsersEntity user = this.userRepository.findById(note.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User who requested to add new note does not exist."));

        log.info("Creating new note entity");
        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setTail(tail);
        noteEntity.setUser(user);
        noteEntity.setContent(note.getContent());
        noteEntity.setCreatedAt(Instant.now());
        log.info("Note is coming from human: {}", note.isHuman());
        noteEntity.setHuman(note.isHuman());
        noteEntity.setN1(false);
        noteEntity.setOrganization(tail.getOrganization());

        // save note
        log.info("Saving new note");
        noteEntity = this.noteRepository.save(noteEntity);
        note.setId(noteEntity.getId());
        note.setUsername(user.getUsername());
        return note;
    }

    @Override
    public Note getById(Long id) throws NoteNotFoundException {
        NoteEntity noteEntity = this.noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Requested not does not exist."));
        return setNote(noteEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> getAllByTailId(Long tailId) {
        log.info("get all notes by tail id: {}", tailId);
        List<NoteEntity> noteEntities = this.noteRepository.findAllByTailIdOrderByCreatedAtAsc(tailId);
        log.info("returning list of notes");
        return noteEntities
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

    @Override
    @Transactional(readOnly = true)
    public Note getIsN1ByTailId(Long tailId) throws NoteNotFoundException {
        List<NoteEntity> notes = this.noteRepository.findTopN1ByTailIdWithFetch(tailId, PageRequest.of(0, 1));
        if (notes.isEmpty()) {
            throw new NoteNotFoundException("No n1 note exists.");
        }
        NoteEntity noteEntity = notes.get(0);
        return setNote(noteEntity);
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
