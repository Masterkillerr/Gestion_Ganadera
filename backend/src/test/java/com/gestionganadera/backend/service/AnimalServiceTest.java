package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAnimalRequest;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private RazaRepository razaRepository;
    @Mock
    private SexoRepository sexoRepository;
    @Mock
    private EstadoAnimalRepository estadoAnimalRepository;

    @InjectMocks
    private AnimalService animalService;

    private Raza raza;
    private Sexo sexo;
    private EstadoAnimal estado;
    private Animal animal;
    private Animal madre;
    private Animal padre;

    @BeforeEach
    void setUp() {
        raza = new Raza(100, "Holstein");
        sexo = new Sexo(1, "Hembra");
        estado = new EstadoAnimal(1, "Saludable");
        madre = new Animal();
        madre.setId(50);
        madre.setNombre("Madre");
        padre = new Animal();
        padre.setId(51);
        padre.setNombre("Padre");
        animal = new Animal();
        animal.setId(1);
        animal.setNombre("Animal Test");
        animal.setIdentificadorArete("AR-1");
        animal.setRaza(raza);
        animal.setSexo(sexo);
        animal.setEstadoAnimal(estado);
    }

    @Test
    void findAll_returnsAllAnimals() {
        Page<Animal> page = new PageImpl<>(List.of(animal));
        when(animalRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Animal> result = animalService.findAll(Pageable.ofSize(20));

        assertEquals(1, result.getContent().size());
        assertEquals("Animal Test", result.getContent().get(0).getNombre());
    }

    @Test
    void findById_existingAnimal_returnsAnimal() {
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        Optional<Animal> result = animalService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Animal Test", result.get().getNombre());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        when(animalRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Animal> result = animalService.findById(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void save_withAllFields_createsAnimal() {
        when(razaRepository.findById(100)).thenReturn(Optional.of(raza));
        when(sexoRepository.findById(1)).thenReturn(Optional.of(sexo));
        when(estadoAnimalRepository.findById(1)).thenReturn(Optional.of(estado));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setIdentificadorArete("AR-123");
        request.setNombre("Vaca Lola");
        request.setSexoId(1);
        request.setEstadoAnimalId(1);
        request.setRazaId(100);
        request.setFechaNacimiento(LocalDate.of(2023, 1, 15));
        request.setPesoActualKg(BigDecimal.valueOf(450));

        Animal saved = new Animal();
        saved.setId(2);
        saved.setIdentificadorArete("AR-123");
        saved.setNombre("Vaca Lola");

        when(animalRepository.save(any(Animal.class))).thenReturn(saved);

        Animal result = animalService.save(request);

        assertEquals("Vaca Lola", result.getNombre());
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void save_withParents_linksParents() {
        when(animalRepository.findById(50)).thenReturn(Optional.of(madre));
        when(animalRepository.findById(51)).thenReturn(Optional.of(padre));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Becerro");
        request.setRazaId(100);
        request.setFechaNacimiento(LocalDate.of(2024, 1, 1));
        request.setIdentificadorArete("AR-99");
        request.setMadreId(50);
        request.setPadreId(51);

        when(razaRepository.findById(100)).thenReturn(Optional.of(raza));

        Animal saved = new Animal();
        saved.setId(3);
        saved.setNombre("Becerro");
        saved.setMadre(madre);
        saved.setPadre(padre);

        when(animalRepository.save(any(Animal.class))).thenReturn(saved);

        Animal result = animalService.save(request);

        assertEquals("Becerro", result.getNombre());
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void update_existingAnimal_updatesFields() {
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));
        when(razaRepository.findById(100)).thenReturn(Optional.of(raza));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Renombrado");
        request.setRazaId(100);
        request.setIdentificadorArete("AR-001");
        request.setFechaNacimiento(LocalDate.of(2022, 1, 1));

        Animal updated = new Animal();
        updated.setId(1);
        updated.setNombre("Renombrado");
        updated.setRaza(raza);
        updated.setIdentificadorArete("AR-001");

        when(animalRepository.save(any(Animal.class))).thenReturn(updated);

        Animal result = animalService.update(1, request);

        assertEquals("Renombrado", result.getNombre());
    }

    @Test
    void update_nonExistent_throws() {
        when(animalRepository.findById(999)).thenReturn(Optional.empty());

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Ghost");
        request.setIdentificadorArete("XXX");
        request.setFechaNacimiento(LocalDate.now());

        assertThrows(RuntimeException.class, () -> animalService.update(999, request));
    }

    @Test
    void delete_existingAnimal_deletes() {
        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        animalService.delete(1);

        verify(animalRepository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(animalRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> animalService.delete(999));
        verify(animalRepository, never()).deleteById(any());
    }
}
