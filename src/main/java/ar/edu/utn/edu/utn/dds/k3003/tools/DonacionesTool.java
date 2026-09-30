package ar.edu.utn.edu.utn.dds.k3003.tools;

import ar.edu.utn.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;

import java.util.Optional;

/**
 * Interfaz (tool) que expone las operaciones del módulo Donaciones.
 * Tu lógica de negocio debe depender de esta interfaz, no del cliente Feign.
 */
public interface DonacionesTool {
    Optional<DonacionDTO> buscarDonacionPorId(Long id);
}
