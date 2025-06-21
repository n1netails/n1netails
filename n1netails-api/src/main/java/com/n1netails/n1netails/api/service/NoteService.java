package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
import com.n1netails.n1netails.api.exception.type.NoteNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.Note;
import com.n1netails.n1netails.api.model.request.NotePageRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NoteService {

    Note add(Note note) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException;
    Note getById(Long id) throws NoteNotFoundException;
    List<Note> getAllByTailId(Long tailId);
    List<Note> getLast9NotesByTailId(Long tailId);
    Page<Note> getNotesByTailId(NotePageRequest request);

    Note getIsN1ByTailId(Long tailId) throws NoteNotFoundException;

}
