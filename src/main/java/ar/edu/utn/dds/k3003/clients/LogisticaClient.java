package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.dtos.logistica.StockDisponibleDTO;
import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
import ar.edu.utn.dds.k3003.requests.DepositoRequest;
import ar.edu.utn.dds.k3003.requests.PaqueteRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LogisticaClient {

    private final RestClient restClient;

    public LogisticaClient(@Value("${LOGISTICA_API_URL}") String baseUrl) {
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

    public Boolean reportarEntrega(PaqueteRequest request){
        try {
            this.restClient.post()
                    .uri("/entregas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.BadRequest br) {
            return false;
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (reportar entrega): " + msg, ex);
        }
    }

    public Optional<List<DepositoDTO>> obtenerTodosLosDepositos() {
        try {
            List<DepositoDTO> lista = restClient.get()
                    .uri("/depositos")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DepositoDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (depositos): " + msg, ex);
        }
    }

    public Optional<DepositoDTO> buscarDepositoPorId(String id) {
        try {
            DepositoDTO dto = restClient.get()
                    .uri("/depositos/{id}", id)
                    .retrieve()
                    .body(DepositoDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (deposito por id): " + msg, ex);
        }
    }

    public Optional<DepositoDTO> crearDeposito(DepositoRequest request) {
        try {
            DepositoDTO dto = restClient.post()
                    .uri("/depositos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(DepositoDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.BadRequest br) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (crear deposito): " + msg, ex);
        }
    }

    public Optional<List<PaqueteDTO>> obtenerTodosLosPaquetes() {
        try {
            List<PaqueteDTO> lista = restClient.get()
                    .uri("/paquetes")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PaqueteDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (paquetes): " + msg, ex);
        }
    }

    public Optional<List<AsignacionDTO>> obtenerTodasLasAsignaciones() {
        try {
            List<AsignacionDTO> lista = restClient.get()
                    .uri("/asignaciones")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AsignacionDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (asignaciones): " + msg, ex);
        }
    }

    public Optional<AsignacionDTO> buscarAsignacionPorId(String id) {
        try {
            AsignacionDTO dto = restClient.get()
                    .uri("/asignaciones/{id}", id)
                    .retrieve()
                    .body(AsignacionDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (asignacion por id): " + msg, ex);
        }
    }

    public Optional<StockDisponibleDTO> consultarStockDisponible(String productoID) {
        try {
            StockDisponibleDTO dto = restClient.get()
                    .uri("/stock/{productoID}", productoID)
                    .retrieve()
                    .body(StockDisponibleDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Logistica (stock): " + msg, ex);
        }
    }
}
