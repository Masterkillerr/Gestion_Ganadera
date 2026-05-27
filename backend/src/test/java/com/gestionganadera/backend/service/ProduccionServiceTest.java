package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
class ProduccionServiceTest {

    @Mock
    private ProduccionRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private ProduccionService produccionService;

    @Captor
    private ArgumentCaptor<Produccion> produccionCaptor;

    private Usuario currentUser;
    private Finca finca;
    private Animal animal;
    private Produccion produccion;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        animal = createAnimal(10, "Vaca Test", finca);
        produccion = createProduccion(1, animal, BigDecimal.valueOf(25.5), "Mañana");
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

    private static Produccion createProduccion(Integer id, Animal animal, BigDecimal litros, String turno) {
        Produccion p = new Produccion();
        p.setId(id);
        p.setAnimal(animal);
        p.setLitros(litros);
        p.setTurno(turno);
        p.setFecha(LocalDate.of(2025, 1, 20));
        return p;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsProducciones() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of(animal));
        when(repository.findByAnimalIdInOrderByFechaDesc(List.of(10))).thenReturn(List.of(produccion));

        List<ProduccionDTO> result = produccionService.findAll();

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(25.5), result.get(0).getLitros());
        assertEquals("Mañana", result.get(0).getTurno());
    }

    @Test
    void findAll_noFincas_returnsEmptyList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of());

        assertTrue(produccionService.findAll().isEmpty());
    }

    @Test
    void findAll_noAnimales_returnsEmptyList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of());

        assertTrue(produccionService.findAll().isEmpty());
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsProducciones() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of(produccion));

        List<Produccion> result = produccionService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(25.5), result.get(0).getLitros());
        assertEquals("Mañana", result.get(0).getTurno());
    }

    @Test
    void findByAnimalId_noProducciones_returnsEmptyList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        assertTrue(produccionService.findByAnimalId(10).isEmpty());
    }

    @Test
    void findByAnimalId_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produccionService.findByAnimalId(99));
        verify(repository, never()).findByAnimalId(anyInt());
    }

    // --- create tests ---

    @Test
    void create_createsProduccion() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(10);
        request.setLitros(BigDecimal.valueOf(30.0));
        request.setTurno("Tarde");
        request.setFecha(LocalDate.of(2025, 2, 1));

        Produccion saved = new Produccion();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setLitros(BigDecimal.valueOf(30.0));
        saved.setTurno("Tarde");
        saved.setFecha(LocalDate.of(2025, 2, 1));

        when(repository.save(any(Produccion.class))).thenReturn(saved);

        ProduccionDTO result = produccionService.create(request);

        assertEquals(BigDecimal.valueOf(30.0), result.getLitros());
        assertEquals("Tarde", result.getTurno());
        assertEquals(LocalDate.of(2025, 2, 1), result.getFecha());
        verify(repository).save(any(Produccion.class));
    }

    @Test
    void create_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(99);
        request.setLitros(BigDecimal.TEN);
        request.setFecha(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> produccionService.create(request));
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingProduccion_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(produccion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        produccionService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produccionService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void delete_unauthorizedAnimal_throws() {
        when(repository.findById(1)).thenReturn(Optional.of(produccion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produccionService.delete(1));
        verify(repository, never()).deleteById(anyInt());
    }

    // --- update tests ---

    @Test
    void update_updatesLitrosAndTurno() {
        when(repository.findById(1)).thenReturn(Optional.of(produccion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setLitros(BigDecimal.valueOf(35.0));
        request.setTurno("Tarde");
        request.setFecha(LocalDate.of(2025, 1, 25));

        Produccion updated = createProduccion(1, animal, BigDecimal.valueOf(35.0), "Tarde");
        updated.setFecha(LocalDate.of(2025, 1, 25));
        when(repository.save(any(Produccion.class))).thenReturn(updated);

        ProduccionDTO result = produccionService.update(1, request);

        assertEquals(BigDecimal.valueOf(35.0), result.getLitros());
        assertEquals("Tarde", result.getTurno());
        assertEquals(LocalDate.of(2025, 1, 25), result.getFecha());
    }
}
