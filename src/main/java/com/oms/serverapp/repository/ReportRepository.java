package com.oms.serverapp.repository;

import com.oms.serverapp.model.Report;
import com.oms.serverapp.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.status = ?1")
    List<Report> findReportsByStatus(Status status);
}
