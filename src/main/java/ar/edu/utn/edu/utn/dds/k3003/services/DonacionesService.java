package ar.edu.utn.edu.utn.dds.k3003.services;

import ar.edu.utn.edu.utn.dds.k3003.adapters.DonacionesClientAdapter;
import ar.edu.utn.edu.utn.dds.k3003.clients.DonacionesClient;
import ar.edu.utn.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;
import ar.edu.utn.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio de dominio que llama al cliente Feign y encapsula el manejo de errores.
 */
@Service
public class DonacionesService {

    private final DonacionesClientAdapter adapter;

    public DonacionesService(DonacionesClientAdapter adapter) {
        this.adapter = adapter;
    }

    public Optional<DonacionDTO> buscarDonacionPorId(String id) {
        // Aquí la lógica de negocio si hace falta (p. ej. validaciones)
        return adapter.buscarPorId(id);
    }
}