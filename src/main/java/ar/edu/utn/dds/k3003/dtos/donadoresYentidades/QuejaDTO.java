package ar.edu.utn.dds.k3003.dtos.donadoresYentidades;

import java.time.LocalDate;

public record QuejaDTO(
    String id,
    String donacionID,
    String donadorID,
    LocalDate fecha,
    String descripcion) {}
