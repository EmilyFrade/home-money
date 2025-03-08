package com.homemoney.services.residence;

import com.homemoney.model.residence.Residence;
import com.homemoney.repositories.residence.ResidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResidenceService {

    @Autowired
    private ResidenceRepository residenceRepository;

    public List<Residence> findAll() {
        return residenceRepository.findAll();
    }

    public Residence findById(Long id) {
        return residenceRepository.findById(id).orElse(null);
    }

    public void save(Residence residence) {
        residenceRepository.save(residence);
    }

    public void delete(Long id) {
        residenceRepository.deleteById(id);
    }

    // Método para buscar residência pelo código de convite
    public Optional<Residence> findByInviteCode(String inviteCode) {
        return residenceRepository.findByInviteCode(inviteCode);
    }
}
