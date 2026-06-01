package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.AnimalDTO;
import com.gestionganadera.backend.dto.CreateAnimalRequest;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalControllerTest {

    @Mock
    private AnimalService animalService;

    @InjectMocks
    private AnimalController animalController;

    @Test
    void getAllAnimales_returnsPage() {
        Animal animal = new Animal();
        animal.setId(1);
        animal.setNombre("Vaca");

        Page<Animal> page = new PageImpl<>(List.of(animal));
        String search = null;
        String estado = null;
        String sexo = null;
        Pageable pageable = Pageable.ofSize(20);
        when(animalService.findAllFiltered(search, estado, sexo, pageable)).thenReturn(page);

        ResponseEntity<Page<AnimalDTO>> response = animalController.getAllAnimales(search, estado, sexo, pageable);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Vaca", response.getBody().getContent().get(0).getNombre());
    }

    @Test
    void getAnimalById_found_returns200() {
        Animal animal = new Animal();
        animal.setId(1);
        animal.setNombre("Vaca");
        when(animalService.findById(1)).thenReturn(Optional.of(animal));

        ResponseEntity<AnimalDTO> response = animalController.getAnimalById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Vaca", response.getBody().getNombre());
    }

    @Test
    void getAnimalById_notFound_returns404() {
        when(animalService.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<AnimalDTO> response = animalController.getAnimalById(999);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void createAnimal_returnsCreated() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Nuevo");
        request.setIdentificadorArete("AR-001");
        request.setRazaId(1);
        request.setFechaNacimiento(LocalDate.of(2024, 1, 1));

        Animal saved = new Animal();
        saved.setId(2);
        saved.setNombre("Nuevo");

        when(animalService.save(request)).thenReturn(saved);

        ResponseEntity<AnimalDTO> response = animalController.createAnimal(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Nuevo", response.getBody().getNombre());
    }

    @Test
    void updateAnimal_returnsUpdated() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Modificado");
        request.setIdentificadorArete("AR-001");
        request.setRazaId(1);
        request.setFechaNacimiento(LocalDate.of(2024, 1, 1));

        Animal updated = new Animal();
        updated.setId(1);
        updated.setNombre("Modificado");

        when(animalService.update(1, request)).thenReturn(updated);

        ResponseEntity<AnimalDTO> response = animalController.updateAnimal(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Modificado", response.getBody().getNombre());
    }

    @Test
    void deleteAnimal_returns204() {
        doNothing().when(animalService).delete(1);

        ResponseEntity<Void> response = animalController.deleteAnimal(1);

        assertEquals(204, response.getStatusCode().value());
        verify(animalService).delete(1);
    }
}
