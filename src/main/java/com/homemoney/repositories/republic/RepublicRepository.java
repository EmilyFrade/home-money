package com.homemoney.repositories.republic;

import com.homemoney.model.republic.Republic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepublicRepository extends JpaRepository<Republic, Long> {
}
