package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.incentivos.DonadorIncentivosDetalleDTO;
import ar.edu.utn.dds.k3003.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.exceptions.RemoteServiceException;
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
public class IncentivosClient {

    private final RestClient restClient;

    public IncentivosClient(@Value("${INCENTIVOS_API_URL}") String baseUrl) {
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

    public Optional<List<DonadorIncentivosDetalleDTO>> listarDonadoresIncentivos() {
        try {
            List<DonadorIncentivosDetalleDTO> lista = restClient.get()
                    .uri("/incentivos-donador")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DonadorIncentivosDetalleDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Incentivos (donadores): " + msg, ex);
        }
    }

    public Optional<DonadorIncentivosDetalleDTO> obtenerDetalleDonador(String donadorId) {
        try {
            DonadorIncentivosDetalleDTO dto = restClient.get()
                    .uri("/incentivos-donador/{id}", donadorId)
                    .retrieve()
                    .body(DonadorIncentivosDetalleDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Incentivos (detalle donador): " + msg, ex);
        }
    }

    public Optional<List<InsigniaDTO>> obtenerInsigniasDeDonador(String donadorId) {
        try {
            List<InsigniaDTO> lista = restClient.get()
                    .uri("/incentivos-donador/{id}/insignias", donadorId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<InsigniaDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Incentivos (insignias): " + msg, ex);
        }
    }

    public Optional<MisionDTO> obtenerMisionEnCursoDeDonador(String donadorId) {
        try {
            MisionDTO dto = restClient.get()
                    .uri("/incentivos-donador/{id}/mision", donadorId)
                    .retrieve()
                    .body(MisionDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Incentivos (mision en curso): " + msg, ex);
        }
    }

    public Optional<List<MisionDTO>> listarMisiones() {
        try {
            List<MisionDTO> lista = restClient.get()
                    .uri("/misiones")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MisionDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Incentivos (misiones): " + msg, ex);
        }
    }

    public Optional<MisionDTO> crearMision(MisionDTO misionDTO) {
        try {
            MisionDTO dto = restClient.post()
                    .uri("/misiones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(misionDTO)
                    .retrieve()
                    .body(MisionDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.BadRequest br) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API Incentivos (crear mision): " + msg, ex);
        }
    }
}
