package ar.edu.utn.dds.k3003.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pregunta por el API Gateway si cada módulo está sano: {@code GET /api/<modulo>/actuator/health}. */
@Service
public class EstadoSistemaClient {

    private final RestClient restClient;

    public EstadoSistemaClient(@Value("${GATEWAY_URL}") String baseUrl) {
        this.restClient = ClienteHttp.crear(baseUrl, 200000);
    }

    /** Nunca lanza: si el módulo no responde, el resultado lo dice. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> salud(String modulo) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("modulo", modulo);
        long inicio = System.currentTimeMillis();
        try {
            ResponseEntity<Map> respuesta = restClient.get()
                    .uri("/api/{modulo}/actuator/health", modulo)
                    .retrieve()
                    .onStatus(estado -> true, (request, response) -> { })
                    .toEntity(Map.class);
            Map<String, Object> cuerpo = respuesta.getBody() == null ? Map.of() : respuesta.getBody();
            resultado.put("httpStatus", respuesta.getStatusCode().value());
            resultado.put("estado", cuerpo.get("status") != null
                    ? String.valueOf(cuerpo.get("status"))
                    : (respuesta.getStatusCode().is2xxSuccessful() ? "UP" : "SIN ESTADO"));
            List<String> caidos = new ArrayList<>();
            if (cuerpo.get("components") instanceof Map<?, ?> componentes) {
                componentes.forEach((nombre, detalle) -> {
                    if (detalle instanceof Map<?, ?> d && "DOWN".equals(d.get("status"))) {
                        caidos.add(String.valueOf(nombre));
                    }
                });
            }
            if (!caidos.isEmpty()) {
                resultado.put("componentesCaidos", caidos);
            }
        } catch (Exception ex) {
            resultado.put("estado", "SIN RESPUESTA");
            resultado.put("detalle", ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
        }
        resultado.put("segundos", (System.currentTimeMillis() - inicio) / 1000);
        return resultado;
    }
}
