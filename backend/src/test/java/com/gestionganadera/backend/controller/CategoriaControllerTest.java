package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateCategoriaRequest;
import com.gestionganadera.backend.model.Categoria;
import com.gestionganadera.backend.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController controller;

    @Test
    void findAll_returnsList() {
        Categoria cat = new Categoria();
        cat.setId(1);
        cat.setNombre("Bovino");
        when(categoriaService.findAll()).thenReturn(List.of(cat));

        ResponseEntity<List<Categoria>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createCategoria_returnsCreated() {
        CreateCategoriaRequest request = new CreateCategoriaRequest();
        request.setNombre("Porcino");

        Categoria saved = new Categoria();
        saved.setId(2);
        saved.setNombre("Porcino");
        when(categoriaService.save(request)).thenReturn(saved);

        ResponseEntity<Categoria> response = controller.createCategoria(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Porcino", response.getBody().getNombre());
    }
}
