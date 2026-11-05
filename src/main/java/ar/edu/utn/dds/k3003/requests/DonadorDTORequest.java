package ar.edu.utn.dds.k3003.requests;

import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.EstadoDonadorEnum;

public record DonadorDTORequest(
        String nombre,
        String apellido,
        Integer edad,
        String email,
        String nroDocumento,
        String domicilio,
        EstadoDonadorEnum estado,
        String categoria
) {}
