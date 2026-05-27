package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
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
class TratamientoServiceTest {

    @Mock
    private TratamientoRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private FincaRepository fincaRepository;
    @Mock
    private MedicamentoRepository medicamentoRepository;

    @InjectMocks
    private TratamientoService tratamientoService;

    private Usuario currentUser;
    private Finca finca;
    private Animal animal;
    private Medicamento medicamento;
    private Tratamiento tratamiento;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        animal = createAnimal(10, "Vaca Test", finca);
        medicamento = createMedicamento(100, "Ivermectina");
        tratamiento = createTratamiento(1, animal, medicamento);
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

    private static Medicamento createMedicamento(Integer id, String nombre) {
        Medicamento m = new Medicamento();
        m.setId(id);
        m.setNombre(nombre);
        return m;
    }

    private static Tratamiento createTratamiento(Integer id, Animal animal, Medicamento medicamento) {
        Tratamiento t = new Tratamiento();
        t.setId(id);
        t.setAnimal(animal);
        t.setMedicamento(medicamento);
        t.setDosis("10ml");
        t.setFechaInicio(LocalDate.of(2025, 1, 1));
        t.setFechaFin(LocalDate.of(2025, 1, 15));
        t.setDiasRetiro(30);
        t.setObservaciones("Observacion test");
        return t;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsTratamientos() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of(tratamiento));

        List<Tratamiento> result = tratamientoService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals("Ivermectina", result.get(0).getMedicamento().getNombre());
        assertEquals("10ml", result.get(0).getDosis());
    }

    @Test
    void findByAnimalId_noTratamientos_returnsEmptyList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        List<Tratamiento> result = tratamientoService.findByAnimalId(10);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByAnimalId_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.findByAnimalId(99));
        verify(repository, never()).findByAnimalId(anyInt());
    }

    // --- save tests ---

    @Test
    void save_createsTratamiento() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(medicamentoRepository.findById(100)).thenReturn(Optional.of(medicamento));

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setAnimalId(10);
        request.setMedicamentoId(100);
        request.setDosis("20ml");
        request.setFechaInicio(LocalDate.of(2025, 2, 1));
        request.setFechaFin(LocalDate.of(2025, 2, 14));
        request.setDiasRetiro(15);
        request.setObservaciones("Nuevo tratamiento");

        Tratamiento saved = new Tratamiento();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setMedicamento(medicamento);
        saved.setDosis("20ml");
        saved.setFechaInicio(LocalDate.of(2025, 2, 1));
        saved.setFechaFin(LocalDate.of(2025, 2, 14));
        saved.setDiasRetiro(15);
        saved.setObservaciones("Nuevo tratamiento");

        when(repository.save(any(Tratamiento.class))).thenReturn(saved);

        Tratamiento result = tratamientoService.save(request);

        assertEquals("20ml", result.getDosis());
        assertEquals(15, result.getDiasRetiro());
        assertEquals("Nuevo tratamiento", result.getObservaciones());
        verify(repository).save(any(Tratamiento.class));
    }

    @Test
    void save_withoutOptionalFields_savesSuccessfully() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(medicamentoRepository.findById(100)).thenReturn(Optional.of(medicamento));

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setAnimalId(10);
        request.setMedicamentoId(100);

        Tratamiento saved = new Tratamiento();
        saved.setId(3);
        saved.setAnimal(animal);
        saved.setMedicamento(medicamento);

        when(repository.save(any(Tratamiento.class))).thenReturn(saved);

        Tratamiento result = tratamientoService.save(request);

        assertNotNull(result);
        verify(repository).save(any(Tratamiento.class));
    }

    @Test
    void save_nonExistentMedicamento_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(medicamentoRepository.findById(999)).thenReturn(Optional.empty());

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setAnimalId(10);
        request.setMedicamentoId(999);
        request.setDosis("5ml");

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.save(request));
        verify(repository, never()).save(any());
    }

    @Test
    void save_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setAnimalId(99);
        request.setMedicamentoId(100);

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.save(request));
        verify(medicamentoRepository, never()).findById(anyInt());
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingTratamiento_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(tratamiento));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        tratamientoService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void delete_unauthorizedAnimal_throws() {
        when(repository.findById(1)).thenReturn(Optional.of(tratamiento));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.delete(1));
        verify(repository, never()).deleteById(anyInt());
    }
}
