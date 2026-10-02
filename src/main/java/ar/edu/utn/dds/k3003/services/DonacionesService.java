package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.clients.DonacionesClient;
import ar.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.requests.DonacionRequest;
import ar.edu.utn.dds.k3003.requests.EstadoDonacionRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio que llama al cliente Feign y encapsula el manejo de errores.
 */
@Service
public class DonacionesService {

    private final DonacionesClient client;

    public DonacionesService(DonacionesClient client) {
        this.client = client;
    }

    public Optional<DonacionDTO> buscarDonacionPorId(String id) {
        return client.consultarDonacionPorID(id);
    }

    public List<DonacionDTO> obtenerTodasLasDonaciones() {
        return client.consultarTodasLasDonaciones().orElseGet(Collections::emptyList);
    }

    public Optional<DonacionDTO> registrarDonacion(DonacionRequest donacionRequest) {
        return client.registrarDonacion(donacionRequest);
    }

    public Optional<DonacionDTO> actualizarEstadoDonacion(String donacionId, EstadoDonacionRequest estadoDonacionRequest) {
        return client.modificarEstadoDonacion(donacionId, estadoDonacionRequest);
    }

}