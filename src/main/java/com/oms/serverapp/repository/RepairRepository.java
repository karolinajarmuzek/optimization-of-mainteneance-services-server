package com.oms.serverapp.repository;

import com.oms.serverapp.model.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {

    @Query(
            value = " select * from repairs where serviceman_id = (select id from servicemen where username=?1)",
            nativeQuery = true)
    List<Repair> findByUsername(String username);

}
