package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAnimalRequest;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
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
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private RazaRepository razaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private LoteRepository loteRepository;
    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private AnimalService animalService;

    private Usuario currentUser;
    private Finca finca;
    private Lote lote;
    private Raza raza;
    private Categoria categoria;
    private Animal animal;
    private Animal madre;
    private Animal padre;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        lote = createLote(10, "Lote A", finca);
        raza = createRaza(100, "Holstein");
        categoria = createCategoria(200, "Vaca Lechera");
        madre = createAnimal(50, "Madre", finca);
        padre = createAnimal(51, "Padre", finca);
        animal = createAnimal(1, "Animal Test", finca);
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

    private static Lote createLote(Integer id, String nombre, Finca finca) {
        Lote l = new Lote();
        l.setId(id);
        l.setNombre(nombre);
        l.setFinca(finca);
        return l;
    }

    private static Raza createRaza(Integer id, String nombre) {
        Raza r = new Raza();
        r.setId(id);
        r.setNombre(nombre);
        return r;
    }

    private static Categoria createCategoria(Integer id, String nombre) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre(nombre);
        return c;
    }

    private static Animal createAnimal(Integer id, String nombre, Finca finca) {
        Animal a = new Animal();
        a.setId(id);
        a.setNombre(nombre);
        a.setFinca(finca);
        a.setSexo("M");
        a.setIdentificadorArete("AR-" + id);
        a.setEstado("Activo");
        return a;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    @Test
    void findAll_returnsAnimalsInUserFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of(animal));

        List<Animal> result = animalService.findAll();

        assertEquals(1, result.size());
        assertEquals("Animal Test", result.get(0).getNombre());
    }

    @Test
    void findById_ownAnimal_returnsAnimal() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.of(animal));

        Optional<Animal> result = animalService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Animal Test", result.get().getNombre());
    }

    @Test
    void save_withAllFields_createsAnimal() {
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));
        when(razaRepository.findById(100)).thenReturn(Optional.of(raza));
        when(categoriaRepository.findById(200)).thenReturn(Optional.of(categoria));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setIdentificadorArete("AR-123");
        request.setNombre("Vaca Lola");
        request.setSexo("H");
        request.setRazaId(100);
        request.setCategoriaId(200);
        request.setFechaNacimiento(LocalDate.of(2023, 1, 15));
        request.setPesoActual(BigDecimal.valueOf(450));
        request.setEstado("Activo");
        request.setFincaId(1);

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
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));

        when(animalRepository.findByIdAndFincaIdIn(50, List.of(1))).thenReturn(Optional.of(madre));
        when(animalRepository.findByIdAndFincaIdIn(51, List.of(1))).thenReturn(Optional.of(padre));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Becerro");
        request.setSexo("M");
        request.setEstado("Activo");
        request.setFincaId(1);
        request.setMadreId(50);
        request.setPadreId(51);
        request.setIdentificadorArete("AR-99");

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
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.of(animal));
        when(razaRepository.findById(100)).thenReturn(Optional.of(raza));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Renombrado");
        request.setSexo("H");
        request.setEstado("Vendido");
        request.setRazaId(100);
        request.setIdentificadorArete("AR-001");

        Animal updated = createAnimal(1, "Renombrado", finca);
        updated.setSexo("H");
        updated.setEstado("Vendido");
        updated.setRaza(raza);

        when(animalRepository.save(any(Animal.class))).thenReturn(updated);

        Animal result = animalService.update(1, request);

        assertEquals("Renombrado", result.getNombre());
        assertEquals("Vendido", result.getEstado());
    }

    @Test
    void update_nonExistent_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Ghost");
        request.setSexo("M");
        request.setEstado("Activo");
        request.setIdentificadorArete("XXX");

        assertThrows(RuntimeException.class, () -> animalService.update(999, request));
    }

    @Test
    void delete_existingAnimal_deletes() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.of(animal));

        animalService.delete(1);

        verify(animalRepository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> animalService.delete(999));
        verify(animalRepository, never()).deleteById(any());
    }

    @Test
    void save_withLoteId_savesWithLote() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(lote));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setIdentificadorArete("AR-456");
        request.setNombre("Vaca con Lote");
        request.setSexo("H");
        request.setEstado("Activo");
        request.setLoteId(10);
        request.setFincaId(1);

        Animal saved = new Animal();
        saved.setId(5);
        saved.setNombre("Vaca con Lote");
        saved.setLote(lote);
        saved.setFinca(finca);

        when(animalRepository.save(any(Animal.class))).thenReturn(saved);

        Animal result = animalService.save(request);

        assertEquals("Vaca con Lote", result.getNombre());
        assertNotNull(result.getLote());
        verify(loteRepository).findByIdAndFincaIdIn(10, List.of(1));
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void update_withAllOptionalFields_updatesEverything() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.of(animal));
        when(categoriaRepository.findById(200)).thenReturn(Optional.of(categoria));
        when(loteRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(lote));
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(50, List.of(1))).thenReturn(Optional.of(madre));
        when(animalRepository.findByIdAndFincaIdIn(51, List.of(1))).thenReturn(Optional.of(padre));

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setIdentificadorArete("AR-999");
        request.setNombre("Completo");
        request.setSexo("H");
        request.setFechaNacimiento(LocalDate.of(2022, 6, 1));
        request.setPesoActual(BigDecimal.valueOf(500));
        request.setEstado("Activo");
        request.setRazaId(100);
        request.setCategoriaId(200);
        request.setLoteId(10);
        request.setFincaId(1);
        request.setMadreId(50);
        request.setPadreId(51);

        Animal updated = createAnimal(1, "Completo", finca);
        updated.setSexo("H");
        updated.setRaza(raza);
        updated.setCategoria(categoria);
        updated.setLote(lote);
        updated.setMadre(madre);
        updated.setPadre(padre);
        updated.setPesoActual(BigDecimal.valueOf(500));

        when(animalRepository.save(any(Animal.class))).thenReturn(updated);

        Animal result = animalService.update(1, request);

        assertEquals("Completo", result.getNombre());
        assertEquals("H", result.getSexo());
        assertNotNull(result.getRaza());
        assertNotNull(result.getCategoria());
        assertNotNull(result.getLote());
        assertNotNull(result.getMadre());
        assertNotNull(result.getPadre());
        verify(categoriaRepository).findById(200);
        verify(loteRepository).findByIdAndFincaIdIn(10, List.of(1));
        verify(fincaRepository).findByIdAndPropietario(1, currentUser);
        verify(animalRepository).findByIdAndFincaIdIn(50, List.of(1));
        verify(animalRepository).findByIdAndFincaIdIn(51, List.of(1));
        verify(animalRepository).save(any(Animal.class));
    }

    @Test
    void update_nonExistentLoteInUpdate_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.of(animal));
        when(loteRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Fails");
        request.setSexo("M");
        request.setEstado("Activo");
        request.setIdentificadorArete("X");
        request.setLoteId(999);

        assertThrows(RuntimeException.class, () -> animalService.update(1, request));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void save_withoutFincaId_doesNotSetFinca() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setIdentificadorArete("AR-NOFINCA");
        request.setNombre("Sin Finca");
        request.setSexo("H");
        request.setEstado("Activo");

        Animal saved = new Animal();
        saved.setId(10);
        saved.setNombre("Sin Finca");

        when(animalRepository.save(any(Animal.class))).thenReturn(saved);

        Animal result = animalService.save(request);

        assertEquals("Sin Finca", result.getNombre());
        verify(animalRepository).save(any(Animal.class));
        verify(fincaRepository, never()).findByIdAndPropietario(any(), any());
    }

    @Test
    void save_withNonExistentLote_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setIdentificadorArete("AR-BADLOTE");
        request.setNombre("Bad Lote");
        request.setSexo("M");
        request.setEstado("Activo");
        request.setLoteId(999);

        assertThrows(RuntimeException.class, () -> animalService.save(request));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void update_nonExistentFincaInUpdate_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(1, List.of(1))).thenReturn(Optional.of(animal));
        when(fincaRepository.findByIdAndPropietario(999, currentUser)).thenReturn(Optional.empty());

        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Fails");
        request.setSexo("M");
        request.setEstado("Activo");
        request.setIdentificadorArete("X");
        request.setFincaId(999);

        assertThrows(RuntimeException.class, () -> animalService.update(1, request));
        verify(animalRepository, never()).save(any());
    }
}
