package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.dto.FincaStatsDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FincaService {

    private final FincaRepository fincaRepository;
    private final AnimalRepository animalRepository;
    private final LoteRepository loteRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    public List<Finca> findAll() {
        return fincaRepository.findByPropietario(getCurrentUser());
    }

    public Optional<Finca> findById(@NonNull Integer id) {
        return fincaRepository.findByIdAndPropietario(id, getCurrentUser());
    }

    public Finca save(@NonNull CreateFincaRequest request) {
        Finca finca = new Finca();
        finca.setNombre(request.getNombre());
        finca.setUbicacion(request.getUbicacion());
        finca.setPropietario(getCurrentUser());
        return fincaRepository.save(finca);
    }

    public Finca update(@NonNull Integer id, @NonNull CreateFincaRequest request) {
        Usuario currentUser = getCurrentUser();
        return fincaRepository.findByIdAndPropietario(id, currentUser)
                .map(existing -> {
                    existing.setNombre(request.getNombre());
                    existing.setUbicacion(request.getUbicacion());
                    return fincaRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    }

    public void delete(@NonNull Integer id) {
        fincaRepository.findByIdAndPropietario(id, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
        fincaRepository.deleteById(id);
    }

    public FincaStatsDTO getStats(@NonNull Integer id) {
        Usuario currentUser = getCurrentUser();
        fincaRepository.findByIdAndPropietario(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        List<Animal> animales = animalRepository.findByFincaId(id);
        List<Lote> lotes = loteRepository.findByFincaId(id);

        FincaStatsDTO stats = new FincaStatsDTO();
        stats.setTotalAnimales(animales.size());
        stats.setMachos(animales.stream().filter(a -> "Macho".equalsIgnoreCase(a.getSexo())).count());
        stats.setHembras(animales.stream().filter(a -> "Hembra".equalsIgnoreCase(a.getSexo())).count());
        stats.setSaludables(animales.stream().filter(a -> "Saludable".equalsIgnoreCase(a.getEstado())).count());
        stats.setEnfermos(animales.stream().filter(a -> "Enfermo".equalsIgnoreCase(a.getEstado())).count());
        stats.setTotalLotes(lotes.size());
        return stats;
    }
}
