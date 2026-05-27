package com.gestionganadera.backend.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDTOsTest {

    // --- CreateAnimalRequest ---

    @Test
    void createAnimalRequest_constructorAndSetters_work() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Vaca Lola");
        request.setSexo("H");
        request.setRazaId(1);
        request.setFincaId(1);
        request.setIdentificadorArete("AR-001");

        assertEquals("Vaca Lola", request.getNombre());
        assertEquals("H", request.getSexo());
        assertEquals(1, request.getRazaId());
        assertEquals(1, request.getFincaId());
        assertEquals("AR-001", request.getIdentificadorArete());
    }

    @Test
    void createAnimalRequest_allArgsConstructor_works() {
        CreateAnimalRequest request = new CreateAnimalRequest(
            "AR-001", "Vaca", "H", 1, 1, 1,
            LocalDate.of(2023, 1, 1), BigDecimal.valueOf(500),
            "Activo", null, null, null, 1
        );

        assertEquals("Vaca", request.getNombre());
        assertEquals(LocalDate.of(2023, 1, 1), request.getFechaNacimiento());
        assertEquals(BigDecimal.valueOf(500), request.getPesoActual());
    }

    // --- CreateFincaRequest ---

    @Test
    void createFincaRequest_constructorAndSetters_work() {
        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Mi Finca");
        request.setUbicacion("Campo Verde");

        assertEquals("Mi Finca", request.getNombre());
        assertEquals("Campo Verde", request.getUbicacion());
    }

    @Test
    void createFincaRequest_allArgsConstructor_works() {
        CreateFincaRequest request = new CreateFincaRequest("Finca Test", "Ubicación");
        assertEquals("Finca Test", request.getNombre());
        assertEquals("Ubicación", request.getUbicacion());
    }

    // --- CreateLoteRequest ---

    @Test
    void createLoteRequest_constructorAndSetters_work() {
        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote A");
        request.setFincaId(1);
        request.setHectareas(BigDecimal.valueOf(50));
        request.setCapacidadMaxima(100);
        request.setTipoPasto("Estrella");
        request.setEstado("Activo");

        assertEquals("Lote A", request.getNombre());
        assertEquals(1, request.getFincaId());
        assertEquals(BigDecimal.valueOf(50), request.getHectareas());
        assertEquals(100, request.getCapacidadMaxima());
        assertEquals("Estrella", request.getTipoPasto());
        assertEquals("Activo", request.getEstado());
    }

    // --- CreateCategoriaRequest ---

    @Test
    void createCategoriaRequest_constructorAndSetters_work() {
        CreateCategoriaRequest request = new CreateCategoriaRequest();
        request.setNombre("Vaca Lechera");
        request.setDescripcion("Alta producción");

        assertEquals("Vaca Lechera", request.getNombre());
        assertEquals("Alta producción", request.getDescripcion());
    }

    // --- CreateRazaRequest ---

    @Test
    void createRazaRequest_constructorAndSetters_work() {
        CreateRazaRequest request = new CreateRazaRequest();
        request.setNombre("Holstein");

        assertEquals("Holstein", request.getNombre());
    }

    @Test
    void createRazaRequest_allArgsConstructor_works() {
        CreateRazaRequest request = new CreateRazaRequest("Brahmán");
        assertEquals("Brahmán", request.getNombre());
    }

    // --- CreateAlimentacionRequest ---

    @Test
    void createAlimentacionRequest_constructorAndSetters_work() {
        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(1);
        request.setAlimentoId(1);
        request.setCantidad(BigDecimal.valueOf(5.5));
        request.setFecha(LocalDate.of(2025, 1, 1));
        request.setObservaciones("Ración diaria");

        assertEquals(1, request.getAnimalId());
        assertEquals(1, request.getAlimentoId());
        assertEquals(BigDecimal.valueOf(5.5), request.getCantidad());
        assertEquals(LocalDate.of(2025, 1, 1), request.getFecha());
        assertEquals("Ración diaria", request.getObservaciones());
    }

    // --- CreateEventoRequest ---

    @Test
    void createEventoRequest_constructorAndSetters_work() {
        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(1);
        request.setTipo("Salud");
        request.setDescripcion("Vacunación");

        assertEquals(1, request.getAnimalId());
        assertEquals("Salud", request.getTipo());
        assertEquals("Vacunación", request.getDescripcion());
    }

    // --- CreateTratamientoRequest ---

    @Test
    void createTratamientoRequest_constructorAndSetters_work() {
        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setAnimalId(1);
        request.setMedicamentoId(1);
        request.setDosis("10ml");
        request.setFechaInicio(LocalDate.of(2025, 1, 1));
        request.setFechaFin(LocalDate.of(2025, 1, 15));
        request.setDiasRetiro(30);
        request.setObservaciones("Tratamiento test");

        assertEquals(1, request.getAnimalId());
        assertEquals("10ml", request.getDosis());
        assertEquals(30, request.getDiasRetiro());
    }

    // --- CreateVacunacionRequest ---

    @Test
    void createVacunacionRequest_constructorAndSetters_work() {
        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setAnimalId(1);
        request.setVacunaId(1);
        request.setFecha(LocalDate.of(2025, 1, 10));
        request.setProximaDosis(LocalDate.of(2025, 7, 10));
        request.setObservaciones("Primera dosis");

        assertEquals(1, request.getAnimalId());
        assertEquals(LocalDate.of(2025, 1, 10), request.getFecha());
        assertEquals(LocalDate.of(2025, 7, 10), request.getProximaDosis());
    }

    // --- CreateProduccionRequest ---

    @Test
    void createProduccionRequest_constructorAndSetters_work() {
        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(1);
        request.setLitros(BigDecimal.valueOf(25.5));
        request.setTurno("Mañana");
        request.setFecha(LocalDate.of(2025, 3, 1));

        assertEquals(1, request.getAnimalId());
        assertEquals(BigDecimal.valueOf(25.5), request.getLitros());
        assertEquals("Mañana", request.getTurno());
        assertEquals(LocalDate.of(2025, 3, 1), request.getFecha());
    }

    // --- ErrorResponse ---

    @Test
    void errorResponse_constructorAndSetters_work() {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(404);
        error.setMessage("Not found");
        error.setTimestamp(123456789L);

        assertEquals(404, error.getStatus());
        assertEquals("Not found", error.getMessage());
        assertEquals(123456789L, error.getTimestamp());
    }

    // --- LoginRequest ---

    @Test
    void loginRequest_constructorAndSetters_work() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");
        request.setRecaptchaToken("token123");

        assertEquals("user@test.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("token123", request.getRecaptchaToken());
    }

    // --- LoginResponse ---

    @Test
    void loginResponse_constructorAndSetters_work() {
        LoginResponse response = new LoginResponse();
        response.setToken("jwt-token");
        response.setEmail("user@test.com");
        response.setRol("USER");
        response.setNombre("Test User");

        assertEquals("jwt-token", response.getToken());
        assertEquals("user@test.com", response.getEmail());
        assertEquals("USER", response.getRol());
        assertEquals("Test User", response.getNombre());
    }

    // --- RegisterRequest ---

    @Test
    void registerRequest_constructorAndSetters_work() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("New User");
        request.setEmail("new@test.com");
        request.setPassword("password123");
        request.setRecaptchaToken("token");

        assertEquals("New User", request.getNombre());
        assertEquals("new@test.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("token", request.getRecaptchaToken());
    }
}
