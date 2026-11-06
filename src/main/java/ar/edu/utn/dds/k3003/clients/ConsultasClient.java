package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Cliente del Servicio de Consultas (modelo de lectura en MongoDB), siempre a través del API Gateway.
 * La base es {@code https://<gateway>/api/consultas/consultas}: el primer "consultas" es el prefijo del
 * gateway y el segundo es parte de la ruta del propio servicio.
 */
@Service
public class ConsultasClient {

    private final RestClient restClient;

    public ConsultasClient(@Value("${CONSULTAS_API_URL}") String baseUrl) {
        this.restClient = ClienteHttp.crear(baseUrl, 120000);
    }

    /** Consulta 1: ficha integral de un donador. Vacío si no existe. */
    public Optional<Object> fichaDonador(String donadorId) {
        return Optional.ofNullable(obtener("/donador/{id}", Map.of(), donadorId));
    }

    public Object listarDonadores() {
        return obtener("/donadores", Map.of());
    }

    /** Consulta 2: necesidades urgentes sin cubrir, con filtros opcionales. */
    public Object necesidadesUrgentes(Integer urgenciaMinima, String tipoNecesidad, String categoriaProducto,
                                      String subcategoriaProducto) {
        Map<String, String> filtros = new LinkedHashMap<>();
        agregar(filtros, "urgenciaMinima", urgenciaMinima == null ? null : String.valueOf(urgenciaMinima));
        agregar(filtros, "tipoNecesidad", tipoNecesidad);
        agregar(filtros, "categoriaProducto", categoriaProducto);
        agregar(filtros, "subcategoriaProducto", subcategoriaProducto);
        return obtener("/necesidades-urgentes", filtros);
    }

    /**
     * Consulta 3: estado de los depósitos. Sin fechas devuelve el estado guardado (todos o uno); con fechas,
     * el tiempo promedio de entrega y las entidades atendidas se calculan solo con las entregas del rango.
     */
    public Object estadoDepositos(String depositoId, String desde, String hasta) {
        boolean hayRango = (desde != null && !desde.isBlank()) || (hasta != null && !hasta.isBlank());
        if (hayRango) {
            Map<String, String> filtros = new LinkedHashMap<>();
            agregar(filtros, "depositoId", depositoId);
            agregar(filtros, "desde", desde);
            agregar(filtros, "hasta", hasta);
            return obtener("/depositos/estado", filtros);
        }
        if (depositoId != null && !depositoId.isBlank()) {
            return obtener("/depositos/{id}", Map.of(), depositoId);
        }
        return obtener("/depositos", Map.of());
    }

    /** Consulta 4: proporción de donaciones con queja por categoría y subcategoría. */
    public Object calidadDonaciones(String categoriaProducto) {
        Map<String, String> filtros = new LinkedHashMap<>();
        agregar(filtros, "categoria", categoriaProducto);
        return obtener("/calidad-donaciones", filtros);
    }

    /** Consulta 4: donadores con más quejas (pipeline de agregación en MongoDB). */
    public Object donadoresConMasQuejas() {
        return obtener("/donadores-con-mas-quejas", Map.of());
    }

    private static void agregar(Map<String, String> filtros, String nombre, String valor) {
        if (valor != null && !valor.isBlank()) {
            filtros.put(nombre, valor);
        }
    }

    private Object obtener(String ruta, Map<String, String> filtros, Object... variables) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(ruta);
                        filtros.forEach(uriBuilder::queryParam);
                        return uriBuilder.build(variables);
                    })
                    .retrieve()
                    .body(Object.class);
        } catch (HttpClientErrorException.NotFound nf) {
            return null;
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar al Servicio de Consultas (" + ruta + "): " + msg, ex);
        }
    }
}
