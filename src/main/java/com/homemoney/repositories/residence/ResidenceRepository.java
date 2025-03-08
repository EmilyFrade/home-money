package com.homemoney.repositories.residence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.homemoney.model.residence.Residence;
import java.util.Optional;

public interface ResidenceRepository extends JpaRepository<Residence, Long> {
    // Metodo para buscar residência por código de convite
    Optional<Residence> findByInviteCode(String inviteCode);
}
