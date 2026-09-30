package ar.edu.utn.edu.utn.dds.k3003.requests;

import java.util.List;

public record DonacionRequest (
        String donadorID, String depositoID, List<DetalleProductoRequest> detallesProductosRequest,
        String descripcion
){}