package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAlimentacionRequest;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlimentacionServiceTest {

    @Mock
    private AlimentacionRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private FincaRepository fincaRepository;
    @Mock
    private AlimentoRepository alimentoRepository;

    @InjectMocks
    private AlimentacionService alimentacionService;

    private Usuario currentUser;
    private Finca finca;
    private Animal animal;
    private Alimento alimento;
    private Alimentacion alimentacion;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        animal = createAnimal(10, "Vaca Test", finca);
        alimento = createAlimento(100, "Pasto");
        alimentacion = createAlimentacion(1, animal, alimento);
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

    private static Alimento createAlimento(Integer id, String nombre) {
        Alimento a = new Alimento();
        a.setId(id);
        a.setNombre(nombre);
        return a;
    }

    private static Alimentacion createAlimentacion(Integer id, Animal animal, Alimento alimento) {
        Alimentacion a = new Alimentacion();
        a.setId(id);
        a.setAnimal(animal);
        a.setAlimento(alimento);
        a.setCantidad(BigDecimal.valueOf(5.5));
        a.setFecha(LocalDate.of(2025, 1, 15));
        a.setObservaciones("Alimentacion test");
        return a;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsAlimentaciones() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of(alimentacion));

        List<Alimentacion> result = alimentacionService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals("Pasto", result.get(0).getAlimento().getNombre());
        assertEquals(BigDecimal.valueOf(5.5), result.get(0).getCantidad());
    }

    @Test
    void findByAnimalId_noAlimentaciones_returnsEmptyList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        assertTrue(alimentacionService.findByAnimalId(10).isEmpty());
    }

    @Test
    void findByAnimalId_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.findByAnimalId(99));
        verify(repository, never()).findByAnimalId(anyInt());
    }

    // --- save tests ---

    @Test
    void save_createsAlimentacion() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(alimentoRepository.findById(100)).thenReturn(Optional.of(alimento));

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(10);
        request.setAlimentoId(100);
        request.setCantidad(BigDecimal.valueOf(8.0));
        request.setFecha(LocalDate.of(2025, 2, 1));
        request.setObservaciones("Racion diaria");

        Alimentacion saved = new Alimentacion();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setAlimento(alimento);
        saved.setCantidad(BigDecimal.valueOf(8.0));
        saved.setFecha(LocalDate.of(2025, 2, 1));
        saved.setObservaciones("Racion diaria");

        when(repository.save(any(Alimentacion.class))).thenReturn(saved);

        Alimentacion result = alimentacionService.save(request);

        assertEquals(BigDecimal.valueOf(8.0), result.getCantidad());
        assertEquals(LocalDate.of(2025, 2, 1), result.getFecha());
        assertEquals("Racion diaria", result.getObservaciones());
        verify(repository).save(any(Alimentacion.class));
    }

    @Test
    void save_withoutOptionalFields_savesSuccessfully() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(alimentoRepository.findById(100)).thenReturn(Optional.of(alimento));

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(10);
        request.setAlimentoId(100);
        request.setFecha(LocalDate.of(2025, 3, 1));

        Alimentacion saved = new Alimentacion();
        saved.setId(3);
        saved.setAnimal(animal);
        saved.setAlimento(alimento);
        saved.setFecha(LocalDate.of(2025, 3, 1));

        when(repository.save(any(Alimentacion.class))).thenReturn(saved);

        Alimentacion result = alimentacionService.save(request);

        assertNotNull(result);
        assertNull(result.getCantidad());
        assertNull(result.getObservaciones());
        verify(repository).save(any(Alimentacion.class));
    }

    @Test
    void save_nonExistentAlimento_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(alimentoRepository.findById(999)).thenReturn(Optional.empty());

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(10);
        request.setAlimentoId(999);
        request.setFecha(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.save(request));
        verify(repository, never()).save(any());
    }

    @Test
    void save_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(99);
        request.setAlimentoId(100);
        request.setFecha(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.save(request));
        verify(alimentoRepository, never()).findById(anyInt());
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingAlimentacion_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(alimentacion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        alimentacionService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void delete_unauthorizedAnimal_throws() {
        when(repository.findById(1)).thenReturn(Optional.of(alimentacion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.delete(1));
        verify(repository, never()).deleteById(anyInt());
    }
}
