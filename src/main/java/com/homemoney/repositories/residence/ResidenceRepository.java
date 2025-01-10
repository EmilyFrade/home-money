package com.homemoney.repositories.residence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homemoney.model.residence.Residence;

public interface ResidenceRepository extends JpaRepository<Residence, Long> {
}
