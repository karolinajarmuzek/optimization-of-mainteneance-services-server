package com.oms.serverapp.repository;

import com.oms.serverapp.model.Device;
import com.oms.serverapp.model.Failure;
import com.oms.serverapp.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findByDeviceAndFailure(Device device, Failure failure);
}
