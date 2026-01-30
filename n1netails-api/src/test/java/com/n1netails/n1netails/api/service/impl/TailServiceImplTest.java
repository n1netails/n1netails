package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.TailStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TailServiceImplTest {

    @Mock
    private TailRepository tailRepository;

    @Mock
    private TailStatusRepository statusRepository;

    @InjectMocks
    private TailServiceImpl tailService;

    @Test
    public void testResolveAll() throws Exception {
        UsersEntity userEntity = new UsersEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        UserPrincipal currentUser = new UserPrincipal(userEntity);

        TailEntity tail1 = new TailEntity();
        tail1.setId(101L);
        TailEntity tail2 = new TailEntity();
        tail2.setId(102L);

        List<TailEntity> newTails = Arrays.asList(tail1, tail2);

        TailStatusEntity resolvedStatus = new TailStatusEntity();
        resolvedStatus.setName("RESOLVED");

        when(tailRepository.findAllByAssignedUserIdAndStatusName(anyLong(), anyString())).thenReturn(newTails);
        when(statusRepository.findTailStatusByName("RESOLVED")).thenReturn(Optional.of(resolvedStatus));

        tailService.resolveAll(currentUser);

        assertEquals("RESOLVED", tail1.getStatus().getName());
        assertEquals("RESOLVED", tail2.getStatus().getName());
        assertNotNull(tail1.getResolvedTimestamp());
        assertNotNull(tail2.getResolvedTimestamp());

        verify(tailRepository, times(1)).saveAll(newTails);
    }
}
