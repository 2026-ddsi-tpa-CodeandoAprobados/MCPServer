package ar.edu.utn.edu.utn.dds.k3003.tools;

import ar.edu.utn.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;

import java.util.Optional;

import ar.edu.utn.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;
import ar.edu.utn.edu.utn.dds.k3003.services.DonacionesService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Tool que expone operaciones del módulo Donaciones para el MCP (Claude Desktop).
 * Los métodos anotados con @Tool son los que quedarán disponibles como herramientas.
 *
 * Nota: devolvemos DonacionDTO o null cuando no se encuentra la donación (404).
 */
@Component
public class DonacionesTool {

    private final DonacionesService donacionesService;

    public DonacionesTool(DonacionesService donacionesService) {
        this.donacionesService = donacionesService;
    }

    @Tool(description = "Busca una donación por su ID")
    public DonacionDTO buscarDonacionPorId(String id) {
        return donacionesService.buscarDonacionPorId(id).orElse(null);
    }

    // Si querés agregar más herramientas, hacelo como más métodos anotados con @Tool.
}
