package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.requests.DonacionRequest;
import ar.edu.utn.dds.k3003.requests.EstadoDonacionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DonacionesClient {

    private final RestClient restClient;

    public DonacionesClient(
            @Value("${DONACIONES_API_URL}") String baseUrl
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                if (connection instanceof javax.net.ssl.HttpsURLConnection httpsConnection) {
                    try {
                        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                            new javax.net.ssl.X509TrustManager() {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                            }
                        };
                        javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
                        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                        httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                        httpsConnection.setHostnameVerifier((hostname, session) -> true);
                    } catch (Exception e) {
                        // ignore
                    }
                }
                super.prepareConnection(connection, httpMethod);
            }
        };
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(60000);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public Optional<DonacionDTO> registrarDonacion(DonacionRequest donacionRequest) {
        try {
            DonacionDTO donacionDTO = restClient.post()
                    .uri("/donaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(donacionRequest)
                    .retrieve()
                    .body(DonacionDTO.class);
            return Optional.ofNullable(donacionDTO);
        } catch (HttpClientErrorException.NotFound | WebClientResponseException.NotFound nf) {
            return Optional.empty();
        } catch (HttpClientErrorException.BadRequest | WebClientResponseException.BadRequest br) {
            return Optional.empty();
        } catch (RestClientException ex) {
            // Errores de transporte/deserialización -> envolvemos para que capas superiores puedan loggear/tratar
            throw new RemoteServiceException("Error al llamar API Donaciones (registrar)", ex);
        } catch (Exception ex) {
            throw new RemoteServiceException("Error inesperado al registrar donación", ex);
        }
    }

    public Optional<DonacionDTO> modificarEstadoDonacion(String donacionID, EstadoDonacionRequest estado) {
        try {
            DonacionDTO dto = restClient.patch()
                    .uri("/donaciones/{donacionID}/estado", donacionID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(estado)
                    .retrieve()
                    .body(DonacionDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound | WebClientResponseException.NotFound nf) {
            return Optional.empty();
        } catch (HttpClientErrorException.BadRequest | WebClientResponseException.BadRequest br) {
            // Cambio de estado inválido según tu controller
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new RemoteServiceException("Error al llamar API Donaciones (modificar estado)", ex);
        } catch (Exception ex) {
            throw new RemoteServiceException("Error inesperado al modificar estado", ex);
        }
    }

    public Optional<List<DonacionDTO>> consultarTodasLasDonaciones(){
        try {
            List<DonacionDTO> donacionesDTOs = restClient.get()
                    .uri("/donaciones")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DonacionDTO>>() {});
            return Optional.ofNullable(donacionesDTOs);
        } catch (HttpClientErrorException.NotFound | WebClientResponseException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Donaciones: " + msg, ex);
        }
    }

    public Optional<DonacionDTO> consultarDonacionPorID(String donacionID) {
        try {
            DonacionDTO donacionDTO = restClient.get()
                    .uri("/donaciones/{id}", donacionID)
                    .retrieve()
                    .body(DonacionDTO.class);
            return Optional.ofNullable(donacionDTO);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Donaciones: " + msg, ex);
        }
    }

    public String modificarEstado(
            String donacionID,
            EstadoDonacionRequest estadoDonacionRequest
    ) {
        try {
            restClient.patch()
                    .uri("/donaciones/{donacionID}/estado", donacionID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(estadoDonacionRequest)
                    .retrieve()
                    .toBodilessEntity();
            return "Donación modificada exitosamente";
        }
        catch (Exception e) {
            return "El cambio de estado no puede llevarse a cabo.";
        }
    }
}