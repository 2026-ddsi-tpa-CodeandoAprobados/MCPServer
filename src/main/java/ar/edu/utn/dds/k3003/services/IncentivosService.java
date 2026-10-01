package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.clients.IncentivosClient;
import ar.edu.utn.dds.k3003.dtos.incentivos.DonadorIncentivosDetalleDTO;
import ar.edu.utn.dds.k3003.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.dtos.incentivos.MisionDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class IncentivosService {

    private final IncentivosClient client;

    public IncentivosService(IncentivosClient client) {
        this.client = client;
    }

    public List<DonadorIncentivosDetalleDTO> listarDonadoresIncentivos() {
        return client.listarDonadoresIncentivos().orElseGet(Collections::emptyList);
    }

    public Optional<DonadorIncentivosDetalleDTO> obtenerDetalleDonador(String donadorId) {
        return client.obtenerDetalleDonador(donadorId);
    }

    public Optional<List<InsigniaDTO>> obtenerInsigniasDeDonador(String donadorId) {
        return client.obtenerInsigniasDeDonador(donadorId);
    }

    public Optional<MisionDTO> obtenerMisionEnCursoDeDonador(String donadorId) {
        return client.obtenerMisionEnCursoDeDonador(donadorId);
    }

    public List<MisionDTO> listarMisiones() {
        return client.listarMisiones().orElseGet(Collections::emptyList);
    }

    public Optional<MisionDTO> crearMision(MisionDTO misionDTO) {
        return client.crearMision(misionDTO);
    }
}
