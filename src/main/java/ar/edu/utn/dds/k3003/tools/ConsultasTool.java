package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.services.ConsultasService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Las cuatro consultas complejas del modelo de lectura (Servicio de Consultas). Se resuelven solo contra
 * MongoDB: los cambios que se acaban de hacer pueden tardar unos segundos en verse (consistencia eventual).
 */
@Component
public class ConsultasTool {

    private final ConsultasService consultasService;

    public ConsultasTool(ConsultasService consultasService) {
        this.consultasService = consultasService;
    }

    @Tool(description = "Consulta 1, ficha integral de un donador: su estado, categoría actual e historial de categorías, "
            + "insignias, misión en curso con su progreso, cantidad de donaciones por estado, quejas recibidas y las "
            + "entidades que recibieron sus donaciones con las unidades entregadas a cada una. Recibe el ID del donador.")
    public ToolResponse<Object> consultarFichaDeDonador(
            @ToolParam(description = "ID del donador") String donadorId) {
        try {
            Optional<Object> ficha = consultasService.fichaDonador(donadorId);
            return ficha.map(f -> new ToolResponse<>(true, "Ficha del donador " + donadorId + ".", f))
                    .orElseGet(() -> new ToolResponse<>(true,
                            "El modelo de lectura no tiene al donador " + donadorId
                                    + " (no existe o sus eventos todavía no llegaron).", null));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, ex.getMessage(), null);
        }
    }

    @Tool(description = "Consulta 1 para todos: las fichas integrales de todos los donadores del modelo de lectura.")
    public ToolResponse<Object> listarFichasDeDonadores() {
        try {
            return respuestaDeLista("fichas de donadores", consultasService.listarDonadores());
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, ex.getMessage(), null);
        }
    }

    @Tool(description = "Consulta 2, necesidades urgentes sin cubrir, ordenadas por urgencia de mayor a menor. Para cada una "
            + "trae la entidad, la cantidad faltante, el stock disponible de ese producto en cada depósito y las unidades "
            + "ya asignadas que todavía no fueron entregadas. Todos los filtros son opcionales.")
    public ToolResponse<Object> consultarNecesidadesUrgentes(
            @ToolParam(description = "Urgencia mínima (solo trae las necesidades con esa urgencia o más)", required = false) Integer urgenciaMinima,
            @ToolParam(description = "Tipo de necesidad: EXTRAORDINARIA o RECURRENTE", required = false) String tipoNecesidad,
            @ToolParam(description = "Categoría del producto", required = false) String categoriaProducto,
            @ToolParam(description = "Subcategoría del producto", required = false) String subcategoriaProducto) {
        try {
            return respuestaDeLista("necesidades urgentes sin cubrir",
                    consultasService.necesidadesUrgentes(urgenciaMinima, tipoNecesidad, categoriaProducto, subcategoriaProducto));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, ex.getMessage(), null);
        }
    }

    @Tool(description = "Consulta 3, estado de los depósitos: ocupación (stock sobre capacidad), algoritmo de matchmaking "
            + "configurado, cantidad de asignaciones por estado, tiempo promedio entre el ingreso de una donación y su "
            + "entrega y cantidad de entidades distintas atendidas. Sin parámetros trae todos los depósitos; con el ID de "
            + "un depósito, solo ese. Con fechas (formato AAAA-MM-DD), el tiempo promedio y las entidades se calculan "
            + "solo con las entregas de ese rango.")
    public ToolResponse<Object> consultarEstadoDeDepositos(
            @ToolParam(description = "ID del depósito (si se omite, todos)", required = false) String depositoId,
            @ToolParam(description = "Fecha desde, AAAA-MM-DD", required = false) String desde,
            @ToolParam(description = "Fecha hasta, AAAA-MM-DD", required = false) String hasta) {
        try {
            Object resultado = consultasService.estadoDepositos(depositoId, desde, hasta);
            if (resultado == null) {
                return new ToolResponse<>(true, "El modelo de lectura no tiene el depósito " + depositoId + ".", null);
            }
            return respuestaDeLista("depósitos", resultado);
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, ex.getMessage(), null);
        }
    }

    @Tool(description = "Consulta 4, calidad de lo donado: por categoría y subcategoría de producto, la proporción de "
            + "donaciones que terminaron con queja. Opcionalmente filtra por categoría.")
    public ToolResponse<Object> consultarCalidadDeDonaciones(
            @ToolParam(description = "Categoría del producto (si se omite, todas)", required = false) String categoria) {
        try {
            return respuestaDeLista("categorías con su proporción de quejas", consultasService.calidadDonaciones(categoria));
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, ex.getMessage(), null);
        }
    }

    @Tool(description = "Consulta 4, donadores con más quejas, de mayor a menor, junto con su estado y su categoría. "
            + "Se resuelve con un pipeline de agregación de MongoDB.")
    public ToolResponse<Object> consultarDonadoresConMasQuejas() {
        try {
            return respuestaDeLista("donadores con quejas", consultasService.donadoresConMasQuejas());
        } catch (RemoteServiceException ex) {
            return new ToolResponse<>(false, ex.getMessage(), null);
        }
    }

    private static ToolResponse<Object> respuestaDeLista(String que, Object resultado) {
        if (resultado instanceof List<?> lista) {
            return new ToolResponse<>(true, lista.isEmpty()
                    ? "No hay " + que + " en el modelo de lectura."
                    : "Se encontraron " + lista.size() + " " + que + ".", resultado);
        }
        return new ToolResponse<>(true, "Resultado de " + que + ".", resultado);
    }
}
