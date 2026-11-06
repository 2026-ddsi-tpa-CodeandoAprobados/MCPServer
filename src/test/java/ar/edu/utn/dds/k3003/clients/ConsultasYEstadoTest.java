package ar.edu.utn.dds.k3003.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.services.EstadoSistemaService;
import ar.edu.utn.dds.k3003.tools.ConsultasTool;
import ar.edu.utn.dds.k3003.tools.ToolResponse;
import ar.edu.utn.dds.k3003.services.ConsultasService;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Las consultas y el estado general salen por el gateway, con las rutas y los filtros correctos. */
class ConsultasYEstadoTest {

    private HttpServer servidor;
    private final List<String> pedidos = new ArrayList<>();
    private final Map<String, Respuesta> respuestas = new LinkedHashMap<>();

    record Respuesta(int codigo, String cuerpo) {}

    @BeforeEach
    void levantar() throws Exception {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/", exchange -> {
            String uri = exchange.getRequestURI().toString();
            pedidos.add(uri);
            Respuesta r = respuestas.getOrDefault(exchange.getRequestURI().getPath(), new Respuesta(404, "{}"));
            byte[] cuerpo = r.cuerpo().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(r.codigo(), cuerpo.length);
            exchange.getResponseBody().write(cuerpo);
            exchange.close();
        });
        servidor.start();
    }

    @AfterEach
    void apagar() {
        servidor.stop(0);
    }

    private String base() {
        return "http://127.0.0.1:" + servidor.getAddress().getPort();
    }

    private ConsultasClient consultas() {
        return new ConsultasClient(base() + "/api/consultas/consultas");
    }

    private static final String RUTA = "/api/consultas/consultas";

    @Test
    void lasNecesidadesUrgentesLlevanSoloLosFiltrosQueSeIndicaron() {
        respuestas.put(RUTA + "/necesidades-urgentes", new Respuesta(200, "[{\"id\":\"n1\",\"urgencia\":8}]"));

        Object resultado = consultas().necesidadesUrgentes(5, "EXTRAORDINARIA", null, "");

        assertEquals(List.of(RUTA + "/necesidades-urgentes?urgenciaMinima=5&tipoNecesidad=EXTRAORDINARIA"), pedidos);
        assertEquals(1, ((List<?>) resultado).size());
    }

    @Test
    void elEstadoDeLosDepositosUsaLaRutaSegunLosParametros() {
        respuestas.put(RUTA + "/depositos", new Respuesta(200, "[]"));
        respuestas.put(RUTA + "/depositos/3", new Respuesta(200, "{\"id\":\"3\"}"));
        respuestas.put(RUTA + "/depositos/estado", new Respuesta(200, "[]"));

        consultas().estadoDepositos(null, null, null);
        consultas().estadoDepositos("3", null, null);
        consultas().estadoDepositos("3", "2026-11-01", "2026-11-07");

        assertEquals(List.of(RUTA + "/depositos", RUTA + "/depositos/3",
                RUTA + "/depositos/estado?depositoId=3&desde=2026-11-01&hasta=2026-11-07"), pedidos);
    }

    @Test
    void laCalidadSePuedeFiltrarPorCategoria() {
        respuestas.put(RUTA + "/calidad-donaciones", new Respuesta(200, "[]"));

        consultas().calidadDonaciones("Alimentos");
        consultas().calidadDonaciones(null);
        respuestas.put(RUTA + "/donadores-con-mas-quejas", new Respuesta(200, "[]"));
        consultas().donadoresConMasQuejas();

        assertEquals(List.of(RUTA + "/calidad-donaciones?categoria=Alimentos", RUTA + "/calidad-donaciones",
                RUTA + "/donadores-con-mas-quejas"), pedidos);
    }

    @Test
    void laFichaDeUnDonadorInexistenteVuelveVacia() {
        respuestas.put(RUTA + "/donador/12", new Respuesta(200, "{\"id\":\"12\",\"estado\":\"VERIFICADO\"}"));

        assertTrue(consultas().fichaDonador("12").isPresent());
        assertFalse(consultas().fichaDonador("99").isPresent());
    }

    @Test
    void siElServicioFallaLaHerramientaLoDiceSinLanzar() {
        respuestas.put(RUTA + "/donadores-con-mas-quejas", new Respuesta(500, "{}"));

        assertThrows(RemoteServiceException.class, () -> consultas().donadoresConMasQuejas());
        ToolResponse<Object> respuesta = new ConsultasTool(new ConsultasService(consultas())).consultarDonadoresConMasQuejas();
        assertFalse(respuesta.isOk());
        assertTrue(respuesta.getMessage().contains("Servicio de Consultas"));
    }

    @Test
    void laHerramientaDeLaFichaExplicaCuandoElDonadorNoEstaEnElModeloDeLectura() {
        ToolResponse<Object> respuesta = new ConsultasTool(new ConsultasService(consultas())).consultarFichaDeDonador("99");

        assertTrue(respuesta.isOk());
        assertTrue(respuesta.getMessage().contains("no tiene al donador 99"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void elEstadoGeneralMarcaQueServicioNoEstaSanoYCuentaElModeloDeLectura() {
        for (String m : List.of("donadores", "incentivos", "logistica", "consultas")) {
            respuestas.put("/api/" + m + "/actuator/health", new Respuesta(200, "{\"status\":\"UP\"}"));
        }
        respuestas.put("/api/donaciones/actuator/health", new Respuesta(503,
                "{\"status\":\"DOWN\",\"components\":{\"db\":{\"status\":\"UP\"},\"rabbit\":{\"status\":\"DOWN\"}}}"));
        respuestas.put(RUTA + "/depositos", new Respuesta(200, "[{\"id\":\"1\"},{\"id\":\"2\"}]"));
        respuestas.put(RUTA + "/donadores", new Respuesta(200, "[{\"id\":\"12\"}]"));
        respuestas.put(RUTA + "/necesidades-urgentes", new Respuesta(200, "[]"));
        respuestas.put(RUTA + "/donadores-con-mas-quejas", new Respuesta(200, "[{\"id\":\"12\"}]"));

        Map<String, Object> estado = new EstadoSistemaService(new EstadoSistemaClient(base()), consultas()).estadoGeneral();

        assertEquals(false, estado.get("todoSano"));
        List<Map<String, Object>> servicios = (List<Map<String, Object>>) estado.get("servicios");
        assertEquals(5, servicios.size());
        Map<String, Object> donaciones = servicios.stream().filter(s -> "donaciones".equals(s.get("modulo"))).findFirst().orElseThrow();
        assertEquals("DOWN", donaciones.get("estado"));
        assertEquals(List.of("rabbit"), donaciones.get("componentesCaidos"));
        assertEquals(4, servicios.stream().filter(s -> "UP".equals(s.get("estado"))).count());
        Map<String, Object> resumen = assertInstanceOf(Map.class, estado.get("modeloDeLectura"));
        assertEquals(2, resumen.get("depositos"));
        assertEquals(1, resumen.get("donadores"));
        assertEquals(0, resumen.get("necesidadesSinCubrir"));
        assertEquals(1, resumen.get("donadoresConQuejas"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void unServicioQueNoRespondeFiguraSinRespuestaYNoRompeElEstado() {
        servidor.stop(0); // nada responde

        Map<String, Object> estado = new EstadoSistemaService(new EstadoSistemaClient(base()), consultas()).estadoGeneral();

        assertEquals(false, estado.get("todoSano"));
        List<Map<String, Object>> servicios = (List<Map<String, Object>>) estado.get("servicios");
        assertTrue(servicios.stream().allMatch(s -> "SIN RESPUESTA".equals(s.get("estado"))));
        assertEquals("no disponible", ((Map<String, Object>) estado.get("modeloDeLectura")).get("depositos"));
    }
}
