package com.oms.serverapp.repository;

import com.oms.serverapp.model.Failure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailureRepository extends JpaRepository<Failure, Long> {
}
