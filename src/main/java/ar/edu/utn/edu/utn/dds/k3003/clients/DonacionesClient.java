package ar.edu.utn.edu.utn.dds.k3003.clients;

import ar.edu.utn.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para Donaciones. La url se toma de la propiedad donaciones.base-url.
 * La configuración específica (interceptor, logging) se referencia en DonacionesFeignConfig.
 */
@FeignClient(name = "donacionesClient", url = "${donaciones.base-url}")
public interface DonacionesClient {
    @GetMapping("/api/donaciones/{id}")
    DonacionDTO getDonacion(@PathVariable("id") Long id);
}

