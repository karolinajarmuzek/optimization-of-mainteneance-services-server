package com.oms.serverapp.repository;

import com.oms.serverapp.model.SparePartNeeded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SparePartNeededRepository extends JpaRepository<SparePartNeeded, Long> {
}
