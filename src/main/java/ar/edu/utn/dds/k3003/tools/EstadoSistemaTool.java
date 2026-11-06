package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.services.EstadoSistemaService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EstadoSistemaTool {

    private final EstadoSistemaService estadoSistemaService;

    public EstadoSistemaTool(EstadoSistemaService estadoSistemaService) {
        this.estadoSistemaService = estadoSistemaService;
    }

    @Tool(description = "Estado general del sistema: si cada servicio (Donaciones, Donadores y Entidades, Incentivos, "
            + "Logística y el Servicio de Consultas) está sano, consultado a través del API Gateway, y cuántos depósitos, "
            + "donadores, necesidades sin cubrir y donadores con quejas hay en el modelo de lectura. Un servicio dormido "
            + "puede tardar hasta 3 minutos en responder.")
    public ToolResponse<Map<String, Object>> consultarEstadoDelSistema() {
        Map<String, Object> estado = estadoSistemaService.estadoGeneral();
        boolean todoSano = Boolean.TRUE.equals(estado.get("todoSano"));
        return new ToolResponse<>(true, todoSano
                ? "Todos los servicios están sanos."
                : "Hay servicios que no están sanos o no respondieron. Mirá el detalle.", estado);
    }
}
