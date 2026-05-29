package com.gestionganadera.backend.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDTOsTest {

    // --- CreateAnimalRequest ---

    @Test
    void createAnimalRequest_constructorAndSetters_work() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setNombre("Vaca Lola");
        request.setSexoId(1);
        request.setEstadoAnimalId(1);
        request.setRazaId(1);
        request.setIdentificadorArete("AR-001");

        assertEquals("Vaca Lola", request.getNombre());
        assertEquals(1, request.getSexoId());
        assertEquals(1, request.getRazaId());
        assertEquals("AR-001", request.getIdentificadorArete());
    }

    @Test
    void createAnimalRequest_allArgsConstructor_works() {
        CreateAnimalRequest request = new CreateAnimalRequest(
            "AR-001", "Vaca", 1, 1, 1,
            LocalDate.of(2023, 1, 1), BigDecimal.valueOf(500),
            null, null, null
        );

        assertEquals("Vaca", request.getNombre());
        assertEquals(LocalDate.of(2023, 1, 1), request.getFechaNacimiento());
        assertEquals(BigDecimal.valueOf(500), request.getPesoActualKg());
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
        request.setDietaId(1);
        request.setFecha(LocalDateTime.of(2025, 1, 1, 8, 0));
        request.setObservacion("Ración diaria");

        assertEquals(1, request.getAnimalId());
        assertEquals(1, request.getDietaId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 8, 0), request.getFecha());
        assertEquals("Ración diaria", request.getObservacion());
    }

    // --- CreateEventoRequest ---

    @Test
    void createEventoRequest_constructorAndSetters_work() {
        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(1);
        request.setTipoEventoId(1);
        request.setDescripcion("Vacunación");

        assertEquals(1, request.getAnimalId());
        assertEquals(1, request.getTipoEventoId());
        assertEquals("Vacunación", request.getDescripcion());
    }

    // --- CreateTratamientoRequest ---

    @Test
    void createTratamientoRequest_constructorAndSetters_work() {
        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setEventoId(1);
        request.setMedicamentoId(1);
        request.setDosisMl("10ml");
        request.setFechaInicio(LocalDate.of(2025, 1, 1));
        request.setFechaFin(LocalDate.of(2025, 1, 15));
        request.setObservacion("Tratamiento test");

        assertEquals(1, request.getEventoId());
        assertEquals("10ml", request.getDosisMl());
    }

    // --- CreateVacunacionRequest ---

    @Test
    void createVacunacionRequest_constructorAndSetters_work() {
        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(1);
        request.setVacunaId(1);
        request.setProximaDosis(LocalDate.of(2025, 7, 10));
        request.setObservacion("Primera dosis");

        assertEquals(1, request.getEventoId());
        assertEquals(LocalDate.of(2025, 7, 10), request.getProximaDosis());
    }

    // --- CreateProduccionRequest ---

    @Test
    void createProduccionRequest_constructorAndSetters_work() {
        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(1);
        request.setLitros(BigDecimal.valueOf(25.5));
        request.setTurnoProduccionId(1);
        request.setFecha(LocalDate.of(2025, 3, 1));

        assertEquals(1, request.getAnimalId());
        assertEquals(BigDecimal.valueOf(25.5), request.getLitros());
        assertEquals(1, request.getTurnoProduccionId());
        assertEquals(LocalDate.of(2025, 3, 1), request.getFecha());
    }

    // --- CreateReproduccionRequest ---

    @Test
    void createReproduccionRequest_constructorAndSetters_work() {
        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setEventoId(1);
        request.setVacaId(1);
        request.setToroId(2);
        request.setTipoReproduccionId(1);
        request.setResultadoReproduccionId(1);
        request.setObservacion("Monta natural");

        assertEquals(1, request.getEventoId());
        assertEquals(1, request.getVacaId());
        assertEquals("Monta natural", request.getObservacion());
    }

    // --- CreatePartoRequest ---

    @Test
    void createPartoRequest_constructorAndSetters_work() {
        CreatePartoRequest request = new CreatePartoRequest();
        request.setEventoId(1);
        request.setReproduccionId(1);
        request.setCantidadCrias(1);
        request.setObservacion("Parto normal");

        assertEquals(1, request.getEventoId());
        assertEquals(1, request.getReproduccionId());
        assertEquals("Parto normal", request.getObservacion());
    }

    // --- CreateMovimientoRequest ---

    @Test
    void createMovimientoRequest_constructorAndSetters_work() {
        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setTipoMovimientoId(1);
        request.setLoteDestinoId(2);
        request.setMotivo("Traslado");

        assertEquals(1, request.getEventoId());
        assertEquals(2, request.getLoteDestinoId());
        assertEquals("Traslado", request.getMotivo());
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
