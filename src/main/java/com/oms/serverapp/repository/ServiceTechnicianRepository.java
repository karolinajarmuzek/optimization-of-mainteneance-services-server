package com.oms.serverapp.repository;

import com.oms.serverapp.model.ServiceTechnician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceTechnicianRepository extends JpaRepository<ServiceTechnician, Long> {
    Optional<ServiceTechnician> findByUsername(String username);
    Boolean existsByUsername(String username);
}
