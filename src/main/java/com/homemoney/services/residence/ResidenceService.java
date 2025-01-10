package com.homemoney.services.residence;

import com.homemoney.model.residence.Residence;
import com.homemoney.repositories.residence.ResidenceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
