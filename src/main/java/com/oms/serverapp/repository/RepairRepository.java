package com.oms.serverapp.repository;

import com.oms.serverapp.model.Repair;
import com.oms.serverapp.model.ServiceTechnician;
import com.oms.serverapp.util.RepairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    Repair findFirstByServiceTechnicianIdOrderByDateDesc(Long serviceTechnicianId);

    List<Repair> findByServiceTechnicianAndStatusNot(ServiceTechnician serviceTechnician, RepairStatus repairStatus);
}
