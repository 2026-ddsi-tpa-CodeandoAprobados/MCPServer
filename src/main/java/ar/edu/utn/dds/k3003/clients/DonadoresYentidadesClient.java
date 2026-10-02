package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.DonadorDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.QuejaDTO;
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
public class DonadoresYentidadesClient {

    private final RestClient restClient;

    public DonadoresYentidadesClient(@Value("${DONADORES_ENTIDADES_API_URL}") String baseUrl) {
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

    public Optional<List<DonadorDTO>> listarDonadores() {
        try {
            List<DonadorDTO> lista = restClient.get()
                    .uri("/donadores")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DonadorDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (donadores): " + msg, ex);
        }
    }

    public Optional<DonadorDTO> buscarDonadorPorId(String donadorID) {
        try {
            DonadorDTO dto = restClient.get()
                    .uri("/donadores/{id}", donadorID)
                    .retrieve()
                    .body(DonadorDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (donador por id): " + msg, ex);
        }
    }

    public Optional<DonadorDTO> registrarDonador(DonadorDTO donadorDTO) {
        try {
            DonadorDTO dto = restClient.post()
                    .uri("/donadores")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(donadorDTO)
                    .retrieve()
                    .body(DonadorDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.BadRequest br) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (registrar donador): " + msg, ex);
        }
    }

    public Optional<DonadorStatsDTO> obtenerEstadisticasDonador(String donadorID) {
        try {
            DonadorStatsDTO dto = restClient.get()
                    .uri("/donadores/{id}/estadisticas", donadorID)
                    .retrieve()
                    .body(DonadorStatsDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (estadisticas): " + msg, ex);
        }
    }

    public Optional<List<EntidadBeneficaDTO>> listarEntidadesBeneficas() {
        try {
            List<EntidadBeneficaDTO> lista = restClient.get()
                    .uri("/entidades")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<EntidadBeneficaDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (entidades): " + msg, ex);
        }
    }

    public Optional<EntidadBeneficaDTO> buscarEntidadPorId(String entidadID) {
        try {
            EntidadBeneficaDTO dto = restClient.get()
                    .uri("/entidades/{id}", entidadID)
                    .retrieve()
                    .body(EntidadBeneficaDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (entidad por id): " + msg, ex);
        }
    }

    public Optional<EntidadBeneficaDTO> registrarEntidadBenefica(EntidadBeneficaDTO entidadDTO) {
        try {
            EntidadBeneficaDTO dto = restClient.post()
                    .uri("/entidades")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(entidadDTO)
                    .retrieve()
                    .body(EntidadBeneficaDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.BadRequest br) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (registrar entidad): " + msg, ex);
        }
    }

    public Optional<List<NecesidadMaterialDTO>> listarNecesidadesMateriales() {
        try {
            List<NecesidadMaterialDTO> lista = restClient.get()
                    .uri("/necesidades")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<NecesidadMaterialDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (necesidades): " + msg, ex);
        }
    }

    public Optional<NecesidadMaterialDTO> registrarNecesidadMaterial(NecesidadMaterialDTO necesidadDTO) {
        try {
            NecesidadMaterialDTO dto = restClient.post()
                    .uri("/necesidades")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(necesidadDTO)
                    .retrieve()
                    .body(NecesidadMaterialDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.BadRequest br) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (registrar necesidad): " + msg, ex);
        }
    }

    public Optional<QuejaDTO> registrarQueja(QuejaDTO quejaDTO) {
        try {
            QuejaDTO dto = restClient.post()
                    .uri("/quejas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(quejaDTO)
                    .retrieve()
                    .body(QuejaDTO.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.BadRequest br) {
            return Optional.empty();
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (registrar queja): " + msg, ex);
        }
    }

    public Optional<List<QuejaDTO>> obtenerQuejasPorDonador(String donadorID) {
        try {
            List<QuejaDTO> lista = restClient.get()
                    .uri("/quejas/{donadorID}", donadorID)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QuejaDTO>>() {});
            return Optional.ofNullable(lista);
        } catch (HttpClientErrorException.NotFound nf) {
            return Optional.of(Collections.emptyList());
        } catch (RestClientException ex) {
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            throw new RemoteServiceException("Error al llamar API DonadoresYEntidades (quejas por donador): " + msg, ex);
        }
    }
}
