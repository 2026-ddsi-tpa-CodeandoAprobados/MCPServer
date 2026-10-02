package ar.edu.utn.dds.k3003.dtos.incentivos;

import java.util.List;

public record DonadorIncentivosDetalleDTO(
        String id,
        MisionDTO misionEnCurso,
        List<MisionDTO> misionesCompletadas,
        List<InsigniaDTO> insignias
) {}
