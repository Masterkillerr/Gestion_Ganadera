package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateEventoRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private EventoService eventoService;

    private Usuario currentUser;
    private Finca finca;
    private Animal animal;
    private Evento evento;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        animal = createAnimal(10, "Vaca Test", finca);
        evento = createEvento(1, animal, "Salud", "Revision veterinaria");
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

    private static Evento createEvento(Integer id, Animal animal, String tipo, String descripcion) {
        Evento e = new Evento();
        e.setId(id);
        e.setAnimal(animal);
        e.setTipo(tipo);
        e.setDescripcion(descripcion);
        e.setFecha(LocalDateTime.of(2025, 1, 15, 10, 0));
        return e;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsEventos() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of(evento));

        List<Evento> result = eventoService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals("Salud", result.get(0).getTipo());
        assertEquals("Revision veterinaria", result.get(0).getDescripcion());
    }

    @Test
    void findByAnimalId_noEventos_returnsEmptyList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        assertTrue(eventoService.findByAnimalId(10).isEmpty());
    }

    @Test
    void findByAnimalId_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventoService.findByAnimalId(99));
        verify(repository, never()).findByAnimalId(anyInt());
    }

    // --- save tests ---

    @Test
    void save_createsEvento() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(10);
        request.setTipo("Reproduccion");
        request.setDescripcion("Inseminacion artificial");

        Evento saved = new Evento();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setTipo("Reproduccion");
        saved.setDescripcion("Inseminacion artificial");
        saved.setFecha(LocalDateTime.now());

        when(repository.save(any(Evento.class))).thenReturn(saved);

        Evento result = eventoService.save(request);

        assertEquals("Reproduccion", result.getTipo());
        assertEquals("Inseminacion artificial", result.getDescripcion());
        verify(repository).save(any(Evento.class));
    }

    @Test
    void save_withoutDescription_savesSuccessfully() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(10);
        request.setTipo("Nota");

        Evento saved = new Evento();
        saved.setId(3);
        saved.setAnimal(animal);
        saved.setTipo("Nota");
        saved.setFecha(LocalDateTime.now());

        when(repository.save(any(Evento.class))).thenReturn(saved);

        Evento result = eventoService.save(request);

        assertEquals("Nota", result.getTipo());
        assertNull(result.getDescripcion());
        verify(repository).save(any(Evento.class));
    }

    @Test
    void save_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(99);
        request.setTipo("Salud");

        assertThrows(EntityNotFoundException.class, () -> eventoService.save(request));
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingEvento_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(evento));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(animal));

        eventoService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventoService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }

    @Test
    void delete_unauthorizedAnimal_throws() {
        when(repository.findById(1)).thenReturn(Optional.of(evento));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventoService.delete(1));
        verify(repository, never()).deleteById(anyInt());
    }
}
