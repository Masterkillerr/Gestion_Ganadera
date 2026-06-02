package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.TipoEventoRepository;
import com.gestionganadera.backend.util.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {
 private final EventoRepository repository;
 private final AnimalRepository animalRepository;
 private final TipoEventoRepository tipoEventoRepository;
 private final UserContext userContext;

 public Page<EventoDTO> findAll(Pageable pageable) {
     Integer userId = userContext.getCurrentUserId();
     return repository.findByUsuarioId(userId, pageable).map(this::toDTO);
 }

 public Evento findById(@NonNull Integer id) {
 return repository.findById(id)
 .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
 }

 public List<EventoDTO> findByAnimalId(@NonNull Integer animalId) {
 return repository.findByAnimalId(animalId).stream()
 .map(this::toDTO)
 .collect(Collectors.toList());
 }

 public List<EventoDTO> getRecent() {
     Integer userId = userContext.getCurrentUserId();
     return repository.findByUsuarioIdOrderByFechaDesc(userId, PageRequest.of(0, 20))
 .map(this::toDTO)
 .getContent();
 }

 @Transactional
 public Evento save(@NonNull CreateEventoRequest request) {
 Animal animal = animalRepository.findById(request.getAnimalId())
 .orElseThrow(() -> new EntityNotFoundException("Animal con ID " + request.getAnimalId() + " no encontrado"));

 Evento entity = new Evento();
 entity.setAnimal(animal);
 entity.setTipoEvento(tipoEventoRepository.findById(request.getTipoEventoId())
 .orElseThrow(() -> new EntityNotFoundException("Tipo de evento con ID " + request.getTipoEventoId() + " no encontrado")));
 entity.setDescripcion(request.getDescripcion());
 entity.setUsuario(userContext.getCurrentUser());
 if (request.getFecha() != null) {
 entity.setFecha(request.getFecha());
 }
 return repository.save(entity);
 }

 @Transactional
 public Evento update(@NonNull Integer id, @NonNull CreateEventoRequest request) {
 Evento entity = repository.findById(id)
 .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con ID " + id));

 Integer userId = userContext.getCurrentUserId();
 if (!entity.getUsuario().getId().equals(userId)) {
 throw new IllegalArgumentException("No tienes permiso para actualizar este evento");
 }

 entity.setAnimal(animalRepository.findById(request.getAnimalId())
 .orElseThrow(() -> new EntityNotFoundException("Animal con ID " + request.getAnimalId() + " no encontrado")));
 entity.setTipoEvento(tipoEventoRepository.findById(request.getTipoEventoId())
 .orElseThrow(() -> new EntityNotFoundException("Tipo de evento con ID " + request.getTipoEventoId() + " no encontrado")));
 entity.setDescripcion(request.getDescripcion());
 if (request.getFecha() != null) {
 entity.setFecha(request.getFecha());
 }
 return repository.save(entity);
 }

 @Transactional
 public void delete(@NonNull Integer id) {
 Evento entity = repository.findById(id)
 .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
 Integer userId = userContext.getCurrentUserId();
 if (!entity.getUsuario().getId().equals(userId)) {
 throw new IllegalArgumentException("No tienes permiso para eliminar este evento");
 }
 repository.deleteById(entity.getId());
 }

 public EventoDTO toDTO(Evento e) {
 EventoDTO dto = new EventoDTO();
 dto.setId(e.getId());
 dto.setFecha(e.getFecha() != null ? e.getFecha().toString() : null);
 String tipo = e.getTipoEvento() != null ? e.getTipoEvento().getNombre() : null;
 dto.setTipo(tipo);
 dto.setTipoEvento(tipo);
 dto.setDescripcion(e.getDescripcion());
 dto.setAnimalId(e.getAnimal() != null ? e.getAnimal().getId() : null);
 dto.setAnimalNombre(e.getAnimal() != null ? e.getAnimal().getNombre() : null);
 dto.setAnimalArete(e.getAnimal() != null ? e.getAnimal().getIdentificadorArete() : null);
 return dto;
 }
}
