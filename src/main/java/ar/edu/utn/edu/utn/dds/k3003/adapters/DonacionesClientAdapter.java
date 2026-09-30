package ar.edu.utn.edu.utn.dds.k3003.adapters;

import ar.edu.utn.edu.utn.dds.k3003.clients.DonacionesClient;
import ar.edu.utn.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;
import ar.edu.utn.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DonacionesClientAdapter {
    private static final Logger log = LoggerFactory.getLogger(DonacionesClientAdapter.class);
    private final DonacionesClient client;

    public DonacionesClientAdapter(DonacionesClient client) {
        this.client = client;
    }

    public Optional<DonacionDTO> buscarPorId(String id) {
        try {
            return Optional.ofNullable(client.buscarDonacionPorID(id));
        } catch (FeignException.NotFound nf) {
            log.debug("Donación {} no encontrada (404): {}", id, nf.getMessage());
            return Optional.empty();
        } catch (FeignException fe) {
            log.error("Error llamando Donaciones API: status={}, msg={}", fe.status(), fe.getMessage());
            throw new RemoteServiceException("Donaciones service error", fe);
        } catch (Exception e) {
            log.error("Error inesperado en adapter Donaciones", e);
            throw new RemoteServiceException("Error inesperado al consultar Donaciones", e);
        }
    }
}
