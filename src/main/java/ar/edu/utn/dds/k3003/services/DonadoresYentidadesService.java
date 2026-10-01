package ar.edu.utn.dds.k3003.services;

import ar.edu.utn.dds.k3003.clients.DonadoresYentidadesClient;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.DonadorDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.dtos.donadoresYentidades.NecesidadMaterialDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service

public class DonadoresYentidadesService {

    private final DonadoresYentidadesClient client;

    public DonadoresYentidadesService(DonadoresYentidadesClient client) {
        this.client = client;
    }

    public List<DonadorDTO> listarDonadores() {
        return client.listarDonadores().orElseGet(Collections::emptyList);
    }

    public Optional<DonadorDTO> buscarDonadorPorId(String donadorID) {
        return client.buscarDonadorPorId(donadorID);
    }

    public Optional<DonadorDTO> registrarDonador(DonadorDTO donadorDTO) {
        return client.registrarDonador(donadorDTO);
    }

    public Optional<DonadorStatsDTO> obtenerEstadisticasDonador(String donadorID) {
        return client.obtenerEstadisticasDonador(donadorID);
    }

    public List<EntidadBeneficaDTO> listarEntidadesBeneficas() {
        return client.listarEntidadesBeneficas().orElseGet(Collections::emptyList);
    }

    public Optional<EntidadBeneficaDTO> buscarEntidadPorId(String entidadID) {
        return client.buscarEntidadPorId(entidadID);
    }

    public Optional<EntidadBeneficaDTO> registrarEntidadBenefica(EntidadBeneficaDTO entidadDTO) {
        return client.registrarEntidadBenefica(entidadDTO);
    }

    public List<NecesidadMaterialDTO> listarNecesidadesMateriales() {
        return client.listarNecesidadesMateriales().orElseGet(Collections::emptyList);
    }

    public Optional<NecesidadMaterialDTO> registrarNecesidadMaterial(NecesidadMaterialDTO necesidadDTO) {
        return client.registrarNecesidadMaterial(necesidadDTO);
    }
}
