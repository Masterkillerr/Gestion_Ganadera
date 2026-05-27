package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateRazaRequest;
import com.gestionganadera.backend.model.Raza;
import com.gestionganadera.backend.repository.RazaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RazaServiceTest {

    @Mock
    private RazaRepository razaRepository;

    @InjectMocks
    private RazaService razaService;

    @Test
    void findAll_returnsAllRazas() {
        Raza raza = new Raza();
        raza.setId(1);
        raza.setNombre("Holstein");

        when(razaRepository.findAll()).thenReturn(List.of(raza));

        List<Raza> result = razaService.findAll();

        assertEquals(1, result.size());
        assertEquals("Holstein", result.get(0).getNombre());
    }

    @Test
    void findAll_emptyList_whenNoRazas() {
        when(razaRepository.findAll()).thenReturn(List.of());

        assertTrue(razaService.findAll().isEmpty());
    }

    @Test
    void save_persistsAndReturnsRaza() {
        CreateRazaRequest request = new CreateRazaRequest();
        request.setNombre("Angus");

        Raza saved = new Raza();
        saved.setId(2);
        saved.setNombre("Angus");

        when(razaRepository.save(any(Raza.class))).thenReturn(saved);

        Raza result = razaService.save(request);

        assertNotNull(result);
        assertEquals("Angus", result.getNombre());
        verify(razaRepository).save(any(Raza.class));
    }
}
