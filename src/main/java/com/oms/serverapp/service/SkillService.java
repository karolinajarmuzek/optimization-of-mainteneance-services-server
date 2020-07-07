package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Device;
import com.oms.serverapp.model.Failure;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.payload.SkillPayload;
import com.oms.serverapp.repository.DeviceRepository;
import com.oms.serverapp.repository.FailureRepository;
import com.oms.serverapp.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class SkillService {

    private SkillRepository skillRepository;
    private DeviceRepository deviceRepository;
    private FailureRepository failureRepository;
    private ServiceHelpers serviceHelpers = new ServiceHelpers();

    @Autowired
    public SkillService(SkillRepository skillRepository, DeviceRepository deviceRepository, FailureRepository failureRepository) {
        this.skillRepository = skillRepository;
        this.deviceRepository = deviceRepository;
        this.failureRepository = failureRepository;
    }

    public List<SkillPayload> getAllSkills() {
        List<Skill> skills = skillRepository.findAll();
        List<SkillPayload> skillsResponse = new ArrayList<>();
        for (Skill skill: skills) {
            skillsResponse.add(new SkillPayload(skill.getId(), skill.getDevice().getId(), skill.getFailure().getId(), skill.getProfit(), skill.getMinRepairTime(), skill.getMaxRepairTime(), serviceHelpers.serviceMenToIds(skill.getServiceMen())));
        }
        return skillsResponse;
    }

    public SkillPayload getSkillById(Long id) throws NotFoundException {
        Skill skill = skillRepository.findById(id).orElse(null);
        if (skill == null) {
            throw new NotFoundException(String.format("Skill with id = %d not found.", id));
        }
        return new SkillPayload(skill.getId(), skill.getDevice().getId(), skill.getFailure().getId(), skill.getProfit(), skill.getMinRepairTime(), skill.getMaxRepairTime(), serviceHelpers.serviceMenToIds(skill.getServiceMen()));
    }

    public ResponseEntity<Skill> addSkill(SkillPayload skillPayload) {
        Device device = deviceRepository.findById(skillPayload.getDevice()).orElse(null);
        Failure failure = failureRepository.findById(skillPayload.getFailure()).orElse(null);
        Skill savedSkill = skillRepository.save(new Skill(device, failure, skillPayload.getProfit(), skillPayload.getMinRepairTime(), skillPayload.getMaxRepairTime(), serviceHelpers.idsToServiceMen(skillPayload.getServiceMen())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedSkill).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateSkill(SkillPayload skillPayload, Long id) {
        Skill skill = skillRepository.findById(id).orElse(null);
        if (skill == null) {
            return ResponseEntity.notFound().build();
        }
        Device device = deviceRepository.findById(skillPayload.getDevice()).orElse(null);
        Failure failure = failureRepository.findById(skillPayload.getFailure()).orElse(null);
        skillRepository.save(new Skill(skill.getId(), device, failure, skillPayload.getProfit(), skillPayload.getMinRepairTime(), skillPayload.getMaxRepairTime(), serviceHelpers.idsToServiceMen(skillPayload.getServiceMen())));
        return ResponseEntity.ok().build();
    }
}
