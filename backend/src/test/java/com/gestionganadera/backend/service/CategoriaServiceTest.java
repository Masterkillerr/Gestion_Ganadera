package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateCategoriaRequest;
import com.gestionganadera.backend.model.Categoria;
import com.gestionganadera.backend.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void findAll_returnsAllCategorias() {
        Categoria cat = new Categoria();
        cat.setId(1);
        cat.setNombre("Ganado Bovino");
        cat.setDescripcion("Bovinos");

        when(categoriaRepository.findAll()).thenReturn(List.of(cat));

        List<Categoria> result = categoriaService.findAll();

        assertEquals(1, result.size());
        assertEquals("Ganado Bovino", result.get(0).getNombre());
        verify(categoriaRepository).findAll();
    }

    @Test
    void findAll_emptyList_whenNoCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<Categoria> result = categoriaService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void save_persistsAndReturnsCategoria() {
        CreateCategoriaRequest request = new CreateCategoriaRequest();
        request.setNombre("Porcino");
        request.setDescripcion("Cerdos");

        Categoria saved = new Categoria();
        saved.setId(2);
        saved.setNombre("Porcino");
        saved.setDescripcion("Cerdos");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(saved);

        Categoria result = categoriaService.save(request);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Porcino", result.getNombre());
        assertEquals("Cerdos", result.getDescripcion());
        verify(categoriaRepository).save(any(Categoria.class));
    }
}
