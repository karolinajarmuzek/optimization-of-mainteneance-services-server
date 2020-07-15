package com.oms.serverapp.repository;

import com.oms.serverapp.model.Repair;
import com.oms.serverapp.model.ServiceMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceManRepository extends JpaRepository<ServiceMan, Long> {
    Optional<ServiceMan> findByUsername(String username);
    Boolean existsByUsername(String username);
}
