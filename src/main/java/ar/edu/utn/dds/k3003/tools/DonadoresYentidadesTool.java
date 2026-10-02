package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.DonadorDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.QuejaDTO;
import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.services.DonadoresYentidadesService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DonadoresYentidadesTool {

    private final DonadoresYentidadesService service;

    public DonadoresYentidadesTool(DonadoresYentidadesService service) {
        this.service = service;
    }

    @Tool(description = "Listar todos los donadores registrados en el sistema.")
    public ToolResponse<List<DonadorDTO>> listarDonadores() {
        try {
            List<DonadorDTO> lista = service.listarDonadores();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay donadores registrados.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " donador(es).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar donadores: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Buscar el perfil de un donador por su ID.")
    public ToolResponse<DonadorDTO> buscarDonadorPorId(String donadorID) {
        try {
            Optional<DonadorDTO> opt = service.buscarDonadorPorId(donadorID);
            return opt.map(dto -> new ToolResponse<>(true, "Donador encontrado.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No existe el donador con id " + donadorID, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar donador: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Registrar un nuevo donador en el sistema. Requiere nombre, apellido, edad, email, nroDocumento, domicilio y estado.")
    public ToolResponse<DonadorDTO> registrarDonador(DonadorDTO donadorDTO) {
        try {
            Optional<DonadorDTO> opt = service.registrarDonador(donadorDTO);
            return opt.map(dto -> new ToolResponse<>(true, "Donador registrado exitosamente.", dto))
                    .orElseGet(() -> new ToolResponse<>(false, "No se pudo registrar el donador. Verifique los datos enviados.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al registrar donador: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Obtener las estadísticas de donaciones acumuladas de un donador por su ID.")
    public ToolResponse<DonadorStatsDTO> obtenerEstadisticasDonador(String donadorID) {
        try {
            Optional<DonadorStatsDTO> opt = service.obtenerEstadisticasDonador(donadorID);
            return opt.map(dto -> new ToolResponse<>(true, "Estadísticas encontradas.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No se encontraron estadísticas para el donador con id " + donadorID, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar estadísticas: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Listar todas las entidades benéficas registradas en el sistema.")
    public ToolResponse<List<EntidadBeneficaDTO>> listarEntidadesBeneficas() {
        try {
            List<EntidadBeneficaDTO> lista = service.listarEntidadesBeneficas();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay entidades benéficas registradas.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " entidad(es) benéfica(s).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar entidades benéficas: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Buscar una entidad benéfica por su ID.")
    public ToolResponse<EntidadBeneficaDTO> buscarEntidadBeneficaPorId(String entidadID) {
        try {
            Optional<EntidadBeneficaDTO> opt = service.buscarEntidadPorId(entidadID);
            return opt.map(dto -> new ToolResponse<>(true, "Entidad benéfica encontrada.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No existe la entidad con id " + entidadID, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar entidad benéfica: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Registrar una nueva entidad benéfica. Requiere razonSocial, domicilio, telefono y correo.")
    public ToolResponse<EntidadBeneficaDTO> registrarEntidadBenefica(EntidadBeneficaDTO entidadDTO) {
        try {
            Optional<EntidadBeneficaDTO> opt = service.registrarEntidadBenefica(entidadDTO);
            return opt.map(dto -> new ToolResponse<>(true, "Entidad benéfica registrada exitosamente.", dto))
                    .orElseGet(() -> new ToolResponse<>(false, "No se pudo registrar la entidad benéfica.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al registrar entidad benéfica: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Listar todas las necesidades materiales publicadas por las entidades benéficas.")
    public ToolResponse<List<NecesidadMaterialDTO>> listarNecesidadesMateriales() {
        try {
            List<NecesidadMaterialDTO> lista = service.listarNecesidadesMateriales();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay necesidades materiales registradas.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " necesidad(es) material(es).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar necesidades materiales: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Registrar una nueva necesidad material para una entidad benéfica. Requiere entidadID, nivelDeUrgencia, descripcion, cantidadObjetivo, productoSolicitadoID y tipo.")
    public ToolResponse<NecesidadMaterialDTO> registrarNecesidadMaterial(NecesidadMaterialDTO necesidadDTO) {
        try {
            Optional<NecesidadMaterialDTO> opt = service.registrarNecesidadMaterial(necesidadDTO);
            return opt.map(dto -> new ToolResponse<>(true, "Necesidad material registrada exitosamente.", dto))
                    .orElseGet(() -> new ToolResponse<>(false, "No se pudo registrar la necesidad material.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al registrar necesidad material: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Registrar una nueva queja de un donador. Requiere donacionID, donadorID, fecha y descripcion.")
    public ToolResponse<QuejaDTO> registrarQueja(QuejaDTO quejaDTO) {
        try {
            Optional<QuejaDTO> opt = service.registrarQueja(quejaDTO);
            return opt.map(dto -> new ToolResponse<>(true, "Queja registrada exitosamente.", dto))
                    .orElseGet(() -> new ToolResponse<>(false, "No se pudo registrar la queja. Verifique los datos enviados.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al registrar queja: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Obtener todas las quejas registradas por un donador específico usando su ID.")
    public ToolResponse<List<QuejaDTO>> obtenerQuejasPorDonador(String donadorID) {
        try {
            List<QuejaDTO> lista = service.obtenerQuejasPorDonador(donadorID);
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay quejas registradas para el donador con id " + donadorID, lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " queja(s).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar quejas: " + ex.getMessage(), null);
        }
    }
}
