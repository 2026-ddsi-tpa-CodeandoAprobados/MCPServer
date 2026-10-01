package ar.edu.utn.dds.k3003.dtos.donadoresYentidades;

public record DonadorDTO(
    String id,
    String nombre,
    String apellido,
    Integer edad,
    String email,
    String nroDocumento,
    String domicilio,
    EstadoDonadorEnum estado,
    String categoria) {}
