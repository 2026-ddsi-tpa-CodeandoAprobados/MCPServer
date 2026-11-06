package ar.edu.utn.dds.k3003;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HerramientasRegistradasTest {

    @Autowired
    private ToolCallbackProvider herramientas;

    @Test
    void elServidorExponeLasConsultasComplejasYElEstadoGeneral() {
        Set<String> nombres = Arrays.stream(herramientas.getToolCallbacks())
                .map(t -> t.getToolDefinition().name())
                .collect(Collectors.toSet());

        assertTrue(nombres.containsAll(Set.of(
                "consultarFichaDeDonador", "listarFichasDeDonadores", "consultarNecesidadesUrgentes",
                "consultarEstadoDeDepositos", "consultarCalidadDeDonaciones", "consultarDonadoresConMasQuejas",
                "consultarEstadoDelSistema")), nombres.toString());
        // y siguen las de los cuatro módulos
        assertTrue(nombres.contains("reportarEntrega"));
        assertTrue(nombres.contains("listarDepositos"));
    }
}
