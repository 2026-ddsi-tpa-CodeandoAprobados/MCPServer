package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.StockDisponibleDTO;
import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.requests.DepositoRequest;
import ar.edu.utn.dds.k3003.requests.PaqueteRequest;
import ar.edu.utn.dds.k3003.services.LogisticaService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LogisticaTool {

    private final LogisticaService logisticaService;

    public LogisticaTool(LogisticaService logisticaService) {
        this.logisticaService = logisticaService;
    }

    @Tool(description = "Reportar entrega de un paquete")
    public ToolResponse<Boolean> reportarEntrega(PaqueteRequest request) {
        try {
            if (logisticaService.reportarEntrega(request)){
                return new ToolResponse<>(true , "La entrega fue realizada correctamente" , true);
            }
            return new ToolResponse<>(false , "La entrega no se puede realizar debido a que el paquete ya fue entregado o no existe" , false);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al intentar reportar la entrega" + ex.getMessage(), false);
        }
    }

    @Tool(description = "Listar todos los depósitos de logística con su capacidad, nombre y stock actual.")
    public ToolResponse<List<DepositoDTO>> listarDepositos() {
        try {
            List<DepositoDTO> lista = logisticaService.listarDepositos();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay depósitos registrados.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " depósito(s).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar depósitos: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Buscar un depósito de logística por su ID.")
    public ToolResponse<DepositoDTO> buscarDepositoPorId(String id) {
        try {
            Optional<DepositoDTO> opt = logisticaService.buscarDepositoPorId(id);
            return opt.map(dto -> new ToolResponse<>(true, "Depósito encontrado.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No existe el depósito con id " + id, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar depósito: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Crear un nuevo depósito de logística. Requiere nombre, dirección y capacidad máxima.")
    public ToolResponse<DepositoDTO> crearDeposito(DepositoRequest depositoRequest) {
        try {
            Optional<DepositoDTO> opt = logisticaService.crearDeposito(depositoRequest);
            return opt.map(dto -> new ToolResponse<>(true, "Depósito creado exitosamente.", dto))
                    .orElseGet(() -> new ToolResponse<>(false, "No se pudo crear el depósito. Verifique los datos enviados.", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al crear depósito: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Listar todos los paquetes registrados en el sistema de logística.")
    public ToolResponse<List<PaqueteDTO>> listarPaquetes() {
        try {
            List<PaqueteDTO> lista = logisticaService.listarPaquetes();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay paquetes registrados.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " paquete(s).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar paquetes: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Listar todas las asignaciones de logística (donaciones asignadas a necesidades).")
    public ToolResponse<List<AsignacionDTO>> listarAsignaciones() {
        try {
            List<AsignacionDTO> lista = logisticaService.listarAsignaciones();
            if (lista.isEmpty()) {
                return new ToolResponse<>(true, "No hay asignaciones registradas.", lista);
            }
            return new ToolResponse<>(true, "Se encontraron " + lista.size() + " asignación(es).", lista);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar asignaciones: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Buscar una asignación de logística por su ID.")
    public ToolResponse<AsignacionDTO> buscarAsignacionPorId(String id) {
        try {
            Optional<AsignacionDTO> opt = logisticaService.buscarAsignacionPorId(id);
            return opt.map(dto -> new ToolResponse<>(true, "Asignación encontrada.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No existe la asignación con id " + id, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar asignación: " + ex.getMessage(), null);
        }
    }

    @Tool(description = "Consultar el stock total disponible de un producto en todos los depósitos. Recibe el ID del producto.")
    public ToolResponse<StockDisponibleDTO> consultarStockDisponible(String productoID) {
        try {
            Optional<StockDisponibleDTO> opt = logisticaService.consultarStockDisponible(productoID);
            return opt.map(dto -> new ToolResponse<>(true, "Stock consultado correctamente.", dto))
                    .orElseGet(() -> new ToolResponse<>(true, "No se encontró stock para el producto " + productoID, null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, "Error al consultar stock: " + ex.getMessage(), null);
        }
    }
}
