package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.clients.LogisticaClient;
import ar.edu.utn.dds.k3003.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.StockDisponibleDTO;
import ar.edu.utn.dds.k3003.requests.DepositoRequest;
import ar.edu.utn.dds.k3003.requests.PaqueteRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LogisticaService {

    private final LogisticaClient client;

    public LogisticaService(LogisticaClient client) {
        this.client = client;
    }

    public Boolean reportarEntrega(PaqueteRequest request){
        return client.reportarEntrega(request);
    }

    public List<DepositoDTO> listarDepositos() {
        return client.obtenerTodosLosDepositos().orElseGet(Collections::emptyList);
    }

    public Optional<DepositoDTO> buscarDepositoPorId(String id) {
        return client.buscarDepositoPorId(id);
    }

    public Optional<DepositoDTO> crearDeposito(DepositoRequest request) {
        return client.crearDeposito(request);
    }

    public List<PaqueteDTO> listarPaquetes() {
        return client.obtenerTodosLosPaquetes().orElseGet(Collections::emptyList);
    }

    public List<AsignacionDTO> listarAsignaciones() {
        return client.obtenerTodasLasAsignaciones().orElseGet(Collections::emptyList);
    }

    public Optional<AsignacionDTO> buscarAsignacionPorId(String id) {
        return client.buscarAsignacionPorId(id);
    }

    public Optional<StockDisponibleDTO> consultarStockDisponible(String productoID) {
        return client.consultarStockDisponible(productoID);
    }
}
