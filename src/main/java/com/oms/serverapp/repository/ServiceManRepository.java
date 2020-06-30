package com.oms.serverapp.repository;

import com.oms.serverapp.model.ServiceMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceManRepository extends JpaRepository<ServiceMan, Long> {

}
