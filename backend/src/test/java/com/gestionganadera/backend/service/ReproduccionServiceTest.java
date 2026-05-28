package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateReproduccionRequest;
import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Reproduccion;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
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
class ReproduccionServiceTest {

    @Mock
    private ReproduccionRepository reproduccionRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private ReproduccionService reproduccionService;

    private Usuario currentUser;
    private Finca finca;
    private Animal vaca;
    private Animal toro;
    private Reproduccion reproduccion;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        vaca = createAnimal(1, "Vaca Lechera");
        vaca.setFinca(finca);
        toro = createAnimal(2, "Toro Bravo");
        toro.setFinca(finca);
        reproduccion = createReproduccion(10, vaca, toro);
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
        return u;
    }

    private static Finca createFinca(Integer id, String nombre, Usuario propietario) {
        Finca f = new Finca();
        f.setId(id);
        f.setNombre(nombre);
        f.setPropietario(propietario);
        return f;
    }

    private static Animal createAnimal(Integer id, String nombre) {
        Animal a = new Animal();
        a.setId(id);
        a.setNombre(nombre);
        a.setIdentificadorArete("AR-" + id);
        a.setSexo(id == 1 ? "H" : "M");
        a.setEstado("Activo");
        return a;
    }

    private static Reproduccion createReproduccion(Integer id, Animal vaca, Animal toro) {
        Reproduccion r = new Reproduccion();
        r.setId(id);
        r.setVaca(vaca);
        r.setToro(toro);
        r.setFechaMonta(LocalDate.of(2026, 5, 1));
        r.setTipo("Natural");
        r.setResultado("Pendiente");
        r.setFechaPartoEstimada(LocalDate.of(2026, 8, 1));
        return r;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    private void mockAuthorization() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(anyInt(), anyList())).thenAnswer(invocation -> {
            Integer animalId = invocation.getArgument(0);
            if (animalId.equals(1)) return Optional.of(vaca);
            if (animalId.equals(2)) return Optional.of(toro);
            return Optional.empty();
        });
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of(vaca, toro));
        when(reproduccionRepository.findByVacaIdInOrderByFechaMontaDesc(List.of(1, 2)))
                .thenReturn(List.of(reproduccion));

        List<ReproduccionDTO> result = reproduccionService.findAll();

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals("Vaca Lechera", result.get(0).getVacaNombre());
    }

    @Test
    void findAll_empty_whenNoAnimals() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of());

        List<ReproduccionDTO> result = reproduccionService.findAll();

        assertTrue(result.isEmpty());
        verify(reproduccionRepository, never()).findByVacaIdInOrderByFechaMontaDesc(anyList());
    }

    @Test
    void findAll_empty_whenNoFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of());

        List<ReproduccionDTO> result = reproduccionService.findAll();

        assertTrue(result.isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsDTO() {
        mockAuthorization();
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));

        ReproduccionDTO result = reproduccionService.findById(10);

        assertEquals(10, result.getId());
        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals("Toro Bravo", result.getToroNombre());
        assertEquals("Natural", result.getTipo());
    }

    @Test
    void findById_nonExistent_throws() {
        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.findById(999));
    }

    @Test
    void findById_unauthorized_throws() {
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> reproduccionService.findById(10));
    }

    // --- create tests ---

    @Test
    void create_createsReproduccion() {
        mockAuthorization();

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setVacaId(1);
        request.setToroId(2);
        request.setFechaMonta(LocalDate.of(2026, 5, 15));
        request.setTipo("Inseminacion");
        request.setResultado("Pendiente");

        when(animalRepository.findById(1)).thenReturn(Optional.of(vaca));
        when(animalRepository.findById(2)).thenReturn(Optional.of(toro));
        when(reproduccionRepository.save(any(Reproduccion.class))).thenAnswer(invocation -> {
            Reproduccion saved = invocation.getArgument(0);
            saved.setId(20);
            return saved;
        });

        ReproduccionDTO result = reproduccionService.create(request);

        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals("Toro Bravo", result.getToroNombre());
        assertEquals("Inseminacion", result.getTipo());
        verify(reproduccionRepository).save(any(Reproduccion.class));
    }

    @Test
    void create_unauthorizedVaca_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(99, List.of(1))).thenReturn(Optional.empty());

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setVacaId(99);
        request.setFechaMonta(LocalDate.now());

        assertThrows(SecurityException.class, () -> reproduccionService.create(request));
        verify(reproduccionRepository, never()).save(any());
    }

    @Test
    void create_vacaNotFound_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setVacaId(999);
        request.setFechaMonta(LocalDate.now());
        request.setTipo("Natural");

        // La primera validación lanza SecurityException porque la vaca no pertenece al usuario
        assertThrows(SecurityException.class, () -> reproduccionService.create(request));
        verify(reproduccionRepository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_updatesFields() {
        mockAuthorization();
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setResultado("Exitoso");
        request.setObservaciones("Todo bien");

        Reproduccion updated = createReproduccion(10, vaca, toro);
        updated.setResultado("Exitoso");
        updated.setObservaciones("Todo bien");
        when(reproduccionRepository.save(any(Reproduccion.class))).thenReturn(updated);

        ReproduccionDTO result = reproduccionService.update(10, request);

        assertEquals("Exitoso", result.getResultado());
        assertEquals("Todo bien", result.getObservaciones());
        verify(reproduccionRepository).save(any(Reproduccion.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setResultado("Exitoso");

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.update(999, request));
        verify(reproduccionRepository, never()).save(any());
    }

    @Test
    void update_unauthorized_throws() {
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.empty());

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setResultado("Exitoso");

        assertThrows(SecurityException.class, () -> reproduccionService.update(10, request));
        verify(reproduccionRepository, never()).save(any());
    }

    @Test
    void update_withNewVacaId_updatesVaca() {
        mockAuthorization();
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));
        when(animalRepository.findById(2)).thenReturn(Optional.of(toro));

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setVacaId(1); // same vaca
        request.setToroId(2);
        request.setResultado("Exitoso");

        Reproduccion updated = createReproduccion(10, vaca, toro);
        updated.setResultado("Exitoso");
        when(reproduccionRepository.save(any(Reproduccion.class))).thenReturn(updated);

        ReproduccionDTO result = reproduccionService.update(10, request);

        assertEquals("Exitoso", result.getResultado());
        verify(reproduccionRepository).save(any(Reproduccion.class));
    }

    // --- delete tests ---

    @Test
    void delete_existing_deletes() {
        mockAuthorization();
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));

        reproduccionService.delete(10);

        verify(reproduccionRepository).delete(reproduccion);
    }

    @Test
    void delete_nonExistent_throws() {
        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.delete(999));
        verify(reproduccionRepository, never()).delete(any());
    }

    @Test
    void delete_unauthorized_throws() {
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> reproduccionService.delete(10));
        verify(reproduccionRepository, never()).delete(any());
    }

    // --- getProximosPartos tests ---

    @Test
    void getProximosPartos_returnsList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of(vaca, toro));
        when(reproduccionRepository.findByFechaPartoEstimadaBetweenAndVacaIdIn(any(), any(), eq(List.of(1, 2))))
                .thenReturn(List.of(reproduccion));

        List<PartosProximosDTO> result = reproduccionService.getProximosPartos();

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getReproduccionId());
        assertEquals("Vaca Lechera", result.get(0).getVacaNombre());
        assertEquals("Toro Bravo", result.get(0).getToroNombre());
        assertNotNull(result.get(0).getDiasRestantes());
    }

    @Test
    void getProximosPartos_empty_whenNoFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of());

        List<PartosProximosDTO> result = reproduccionService.getProximosPartos();

        assertTrue(result.isEmpty());
    }

    @Test
    void getProximosPartos_empty_whenNoAnimals() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of());

        List<PartosProximosDTO> result = reproduccionService.getProximosPartos();

        assertTrue(result.isEmpty());
    }
}
