package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.clients.ConsultasClient;
import ar.edu.utn.dds.k3003.clients.EstadoSistemaClient;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Estado general del sistema: si cada servicio está sano (consultado por el gateway, todos a la vez para no
 * esperar uno por uno a los que estén dormidos) y cuántas cosas hay en el modelo de lectura.
 */
@Service
public class EstadoSistemaService {

    static final List<String> MODULOS = List.of("donaciones", "donadores", "incentivos", "logistica", "consultas");

    private final EstadoSistemaClient estadoClient;
    private final ConsultasClient consultasClient;

    public EstadoSistemaService(EstadoSistemaClient estadoClient, ConsultasClient consultasClient) {
        this.estadoClient = estadoClient;
        this.consultasClient = consultasClient;
    }

    public Map<String, Object> estadoGeneral() {
        List<CompletableFuture<Map<String, Object>>> pedidos = MODULOS.stream()
                .map(m -> CompletableFuture.supplyAsync(() -> estadoClient.salud(m)))
                .toList();
        List<Map<String, Object>> servicios = pedidos.stream().map(CompletableFuture::join).toList();
        boolean todoSano = servicios.stream().allMatch(s -> "UP".equals(s.get("estado")));

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("depositos", contar(() -> consultasClient.estadoDepositos(null, null, null)));
        resumen.put("donadores", contar(consultasClient::listarDonadores));
        resumen.put("necesidadesSinCubrir", contar(() -> consultasClient.necesidadesUrgentes(null, null, null, null)));
        resumen.put("donadoresConQuejas", contar(consultasClient::donadoresConMasQuejas));

        Map<String, Object> estado = new LinkedHashMap<>();
        estado.put("todoSano", todoSano);
        estado.put("servicios", servicios);
        estado.put("modeloDeLectura", resumen);
        return estado;
    }

    private static Object contar(java.util.function.Supplier<Object> consulta) {
        try {
            Object resultado = consulta.get();
            return resultado instanceof List<?> lista ? (Object) lista.size() : "sin datos";
        } catch (Exception ex) {
            return "no disponible";
        }
    }
}
