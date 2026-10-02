package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.dtos.incentivos.DonadorIncentivosDetalleDTO;
import ar.edu.utn.dds.k3003.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.services.IncentivosService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IncentivosToolImpl {

    private final IncentivosService incentivosService;

    public IncentivosToolImpl(IncentivosService incentivosService) {
        this.incentivosService = incentivosService;
    }

    @Tool(description = "Listar todos los donadores registrados en el sistema de incentivos con sus misiones e insignias.")
    public ToolResponse<List<DonadorIncentivosDetalleDTO>> listarDonadoresIncentivos() {
        try {
            List<DonadorIncentivosDetalleDTO> lista = incentivosService.listarDonadoresIncentivos();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay donadores registrados en incentivos.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " donador(es).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar donadores de incentivos: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Obtener el detalle de un donador en el sistema de incentivos por su ID (misión en curso, misiones completadas e insignias).")
    public ToolResponse<DonadorIncentivosDetalleDTO> obtenerDetalleDonadorIncentivos(String donadorId) {
        try {
            Optional<DonadorIncentivosDetalleDTO> opt = incentivosService.obtenerDetalleDonador(donadorId);
            return opt.map(dto -> new ToolResponse<>(true, "Detalle del donador encontrado.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No existe el donador con id " + donadorId + " en incentivos.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar detalle del donador: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Obtener las insignias ganadas por un donador. Recibe el ID del donador.")
    public ToolResponse<List<InsigniaDTO>> obtenerInsigniasDeDonador(String donadorId) {
        try {
            Optional<List<InsigniaDTO>> opt = incentivosService.obtenerInsigniasDeDonador(donadorId);
            return opt.map(lista -> new ToolResponse<>(true, "Se encontraron " + lista.size() + " insignia(s).", lista))
                    .orElseGet(() -> new ToolResponse<>(true, "No se encontraron insignias para el donador con id " + donadorId, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar insignias: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Obtener la misión en curso de un donador. Recibe el ID del donador.")
    public ToolResponse<MisionDTO> obtenerMisionEnCursoDeDonador(String donadorId) {
        try {
            Optional<MisionDTO> opt = incentivosService.obtenerMisionEnCursoDeDonador(donadorId);
            return opt.map(dto -> new ToolResponse<>(true, "Misión en curso encontrada.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "El donador con id " + donadorId + " no tiene misión en curso.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar misión en curso: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Listar todas las misiones disponibles en el sistema de incentivos.")
    public ToolResponse<List<MisionDTO>> listarMisiones() {
        try {
            List<MisionDTO> lista = incentivosService.listarMisiones();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay misiones registradas.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " misión(es).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar misiones: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Crear una nueva misión de incentivos. Requiere nombre, insigniaID, categoriaInicio, categoriaFin y tipo de misión.")
    public ToolResponse<MisionDTO> crearMision(MisionDTO misionDTO) {
        try {
            Optional<MisionDTO> opt = incentivosService.crearMision(misionDTO);
            return opt.map(dto -> new ToolResponse<>(true, "Misión creada exitosamente.", dto))
                    .orElseGet(() -> new ToolResponse<>(false, "No se pudo crear la misión. Verifique los datos enviados.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al crear misión: " + ex.getMessage(), null);
        }
    }
}
