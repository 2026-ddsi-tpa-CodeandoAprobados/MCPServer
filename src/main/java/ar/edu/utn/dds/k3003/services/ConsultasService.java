package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.clients.ConsultasClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsultasService {

    private final ConsultasClient client;

    public ConsultasService(ConsultasClient client) {
        this.client = client;
    }

    public Optional<Object> fichaDonador(String donadorId) {
        return client.fichaDonador(donadorId);
    }

    public Object listarDonadores() {
        return client.listarDonadores();
    }

    public Object necesidadesUrgentes(Integer urgenciaMinima, String tipoNecesidad, String categoriaProducto,
                                      String subcategoriaProducto) {
        return client.necesidadesUrgentes(urgenciaMinima, tipoNecesidad, categoriaProducto, subcategoriaProducto);
    }

    public Object estadoDepositos(String depositoId, String desde, String hasta) {
        return client.estadoDepositos(depositoId, desde, hasta);
    }

    public Object calidadDonaciones(String categoria) {
        return client.calidadDonaciones(categoria);
    }

    public Object donadoresConMasQuejas() {
        return client.donadoresConMasQuejas();
    }
}
