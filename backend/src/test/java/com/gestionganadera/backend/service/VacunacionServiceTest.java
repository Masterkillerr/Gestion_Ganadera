package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacunacionServiceTest {

    @Mock
    private VacunacionRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private FincaRepository fincaRepository;
    @Mock
    private VacunaRepository vacunaRepository;

    @InjectMocks
    private VacunacionService vacunacionService;

    private Usuario currentUser;
    private Finca finca;
    private Animal animal;
    private Vacuna vacuna;
    private Vacunacion vacunacion;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        animal = createAnimal(10, "Vaca Test", finca);
        vacuna = createVacuna(100, "Aftosa");
        vacunacion = createVacunacion(1, animal, vacuna);
        authenticateAs(currentUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static Usuario createUser(String email, String nombre) {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword("encoded");
        u.setRole(new Role(1, "USER"));
        return u;
    }

    private static Finca createFinca(Integer id, String nombre, Usuario propietario) {
        Finca f = new Finca();
        f.setId(id);
        f.setNombre(nombre);
        f.setPropietario(propietario);
        return f;
    }

    private static Animal createAnimal(Integer id, String nombre, Finca finca) {
        Animal a = new Animal();
        a.setId(id);
        a.setNombre(nombre);
        a.setFinca(finca);
        a.setSexo("H");
        a.setIdentificadorArete("AR-" + id);
        a.setEstado("Activo");
        return a;
    }

    private static Vacuna createVacuna(Integer id, String nombre) {
        Vacuna v = new Vacuna();
        v.setId(id);
        v.setNombre(nombre);
        return v;
    }

    private static Vacunacion createVacunacion(Integer id, Animal animal, Vacuna vacuna) {
        Vacunacion v = new Vacunacion();
        v.setId(id);
        v.setAnimal(animal);
        v.setVacuna(vacuna);
        v.setFecha(LocalDate.of(2025, 1, 10));
        v.setProximaDosis(LocalDate.of(2025, 7, 10));
        v.setObservaciones("Vacunacion test");
        return v;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsVacunaciones() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of(vacunacion));

        List<Vacunacion> result = vacunacionService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals("Aftosa", result.get(0).getVacuna().getNombre());
    }

    @Test
    void findByAnimalId_noVacunaciones_returnsEmptyList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        assertTrue(vacunacionService.findByAnimalId(10).isEmpty());
    }

    @Test
    void findByAnimalId_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.findByAnimalId(99));
        verify(repository, never()).findByAnimalId(anyInt());
    }

    // --- save tests ---

    @Test
    void save_createsVacunacion() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(vacunaRepository.findById(100)).thenReturn(Optional.of(vacuna));

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setAnimalId(10);
        request.setVacunaId(100);
        request.setFecha(LocalDate.of(2025, 3, 1));
        request.setProximaDosis(LocalDate.of(2025, 9, 1));
        request.setObservaciones("Refuerzo anual");

        Vacunacion saved = new Vacunacion();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setVacuna(vacuna);
        saved.setFecha(LocalDate.of(2025, 3, 1));
        saved.setProximaDosis(LocalDate.of(2025, 9, 1));
        saved.setObservaciones("Refuerzo anual");

        when(repository.save(any(Vacunacion.class))).thenReturn(saved);

        Vacunacion result = vacunacionService.save(request);

        assertEquals(LocalDate.of(2025, 3, 1), result.getFecha());
        assertEquals(LocalDate.of(2025, 9, 1), result.getProximaDosis());
        assertEquals("Refuerzo anual", result.getObservaciones());
        verify(repository).save(any(Vacunacion.class));
    }

    @Test
    void save_withoutOptionalFields_savesSuccessfully() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(vacunaRepository.findById(100)).thenReturn(Optional.of(vacuna));

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setAnimalId(10);
        request.setVacunaId(100);
        request.setFecha(LocalDate.of(2025, 4, 1));

        Vacunacion saved = new Vacunacion();
        saved.setId(3);
        saved.setAnimal(animal);
        saved.setVacuna(vacuna);
        saved.setFecha(LocalDate.of(2025, 4, 1));

        when(repository.save(any(Vacunacion.class))).thenReturn(saved);

        Vacunacion result = vacunacionService.save(request);

        assertNotNull(result);
        assertNull(result.getProximaDosis());
        assertNull(result.getObservaciones());
        verify(repository).save(any(Vacunacion.class));
    }

    @Test
    void save_nonExistentVacuna_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(vacunaRepository.findById(999)).thenReturn(Optional.empty());

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setAnimalId(10);
        request.setVacunaId(999);
        request.setFecha(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.save(request));
        verify(repository, never()).save(any());
    }

    @Test
    void save_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setAnimalId(99);
        request.setVacunaId(100);
        request.setFecha(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.save(request));
        verify(vacunaRepository, never()).findById(anyInt());
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingVacunacion_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(vacunacion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        vacunacionService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void delete_unauthorizedAnimal_throws() {
        when(repository.findById(1)).thenReturn(Optional.of(vacunacion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.delete(1));
        verify(repository, never()).deleteById(anyInt());
    }
}
