package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;

import java.util.List;
import java.util.Optional;

import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.requests.DonacionRequest;
import ar.edu.utn.dds.k3003.requests.EstadoDonacionRequest;
import ar.edu.utn.dds.k3003.services.DonacionesService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class DonacionesTool {

    private final DonacionesService donacionesService;

    public DonacionesTool(DonacionesService donacionesService) {
        this.donacionesService = donacionesService;
    }

    @Tool(description = "Busca una donación por su ID")
    public ToolResponse<DonacionDTO> buscarDonacionPorId(String id) {
        try {
            Optional<DonacionDTO> opt = donacionesService.buscarDonacionPorId(id);
            return opt.map(dto -> new ToolResponse<>(true, "Donación encontrada.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No existe la donación con id " + id, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar la API: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Buscar todas las donaciones")
    public ToolResponse<List<DonacionDTO>> listarDonaciones() {
        try {
            List<DonacionDTO> lista = donacionesService.obtenerTodasLasDonaciones();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay donaciones registradas.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " donaciones.", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar el servicio de Donaciones: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Registrar una nueva donación. Recibe un DonacionRequest y devuelve el DonacionDTO creado o un mensaje en caso de que no se haya" +
        " podido " + "crear.")
    public ToolResponse<DonacionDTO> registrarDonacion(DonacionRequest donacionRequest) {
        try {
            Optional<DonacionDTO> donacionOpcional = donacionesService.registrarDonacion(donacionRequest);
            if (donacionOpcional.isPresent()) {
                return new ToolResponse<>(true, "Donación registrada con éxito.", donacionOpcional.get());
            } else {
                return new ToolResponse<>(false, "No se pudo registrar la donación: datos inválidos o recurso relacionado no existe.", null);
            }
        } catch (Exception ex) {
            return new ToolResponse<>(false, "Error al registrar la donación: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Cambiar el estado de una donación. Parámetros: donacionId (String), estadoRequest (estadoDonacionRequest).")
    public ToolResponse<DonacionDTO> cambiarEstadoDonacion(String donacionId, EstadoDonacionRequest estadoRequest) {
        try {
            Optional<DonacionDTO> res = donacionesService.actualizarEstadoDonacion(donacionId, estadoRequest);
            if (res.isPresent()) {
                return new ToolResponse<>(true, "Estado actualizado correctamente.", res.get());
            } else {
                // client normalizó 400/404 a Optional.empty()
                return new ToolResponse<>(false, "No se encontró la donación o el cambio de estado no fue válido.", null);
            }
        } catch (Exception ex) {
            return new ToolResponse<>(false, "Error al actualizar el estado: " + ex.getMessage(), null);
        }
    }
}




