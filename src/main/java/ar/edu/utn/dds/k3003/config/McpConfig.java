package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.tools.ConsultasTool;
import ar.edu.utn.dds.k3003.tools.EstadoSistemaTool;
import ar.edu.utn.dds.k3003.tools.DonacionesTool;
import ar.edu.utn.dds.k3003.tools.DonadoresYentidadesTool;
import ar.edu.utn.dds.k3003.tools.IncentivosToolImpl;
import ar.edu.utn.dds.k3003.tools.LogisticaTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider allTools(
            DonacionesTool donacionesTool,
            LogisticaTool logisticaTool,
            IncentivosToolImpl incentivosToolImpl,
            DonadoresYentidadesTool donadoresYentidadesTool,
            ConsultasTool consultasTool,
            EstadoSistemaTool estadoSistemaTool
    ) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(donacionesTool, logisticaTool, incentivosToolImpl, donadoresYentidadesTool,
                        consultasTool, estadoSistemaTool)
                .build();
    }
}