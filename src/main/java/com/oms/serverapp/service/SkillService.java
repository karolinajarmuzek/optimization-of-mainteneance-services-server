package com.oms.serverapp.service;

import com.oms.serverapp.exception.IncorrectRepairTimeException;
import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Device;
import com.oms.serverapp.model.Failure;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.payload.SkillPayload;
import com.oms.serverapp.repository.DeviceRepository;
import com.oms.serverapp.repository.FailureRepository;
import com.oms.serverapp.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private ServiceTechnicianService serviceTechnicianService;

    @Autowired
    public SkillService(SkillRepository skillRepository, DeviceRepository deviceRepository, FailureRepository failureRepository, @Lazy ServiceTechnicianService serviceTechnicianService) {
        this.skillRepository = skillRepository;
        this.deviceRepository = deviceRepository;
        this.failureRepository = failureRepository;
        this.serviceTechnicianService = serviceTechnicianService;
    }

    public List<SkillPayload> getAllSkills() {
        List<Skill> skills = skillRepository.findAll();
        List<SkillPayload> skillsResponse = new ArrayList<>();
        for (Skill skill: skills) {
            skillsResponse.add(new SkillPayload(skill));
        }
        return skillsResponse;
    }

    public SkillPayload getSkillById(Long id) throws NotFoundException {
        Skill skill = skillRepository.findById(id).orElse(null);
        if (skill == null) {
            throw new NotFoundException(String.format("Skill with id = %d not found.", id));
        }
        return new SkillPayload(skill);
    }

    public ResponseEntity<Skill> addSkill(SkillPayload skillPayload) throws IncorrectRepairTimeException {
        if (skillPayload.getMinRepairTime() > skillPayload.getMaxRepairTime())
            throw new IncorrectRepairTimeException();
        Device device = deviceRepository.findById(skillPayload.getDevice()).orElse(null);
        Failure failure = failureRepository.findById(skillPayload.getFailure()).orElse(null);
        Skill savedSkill = skillRepository.save(new Skill(skillPayload, device, failure, serviceTechnicianService.idsToServiceTechnician(skillPayload.getServiceTechnicians())));
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
        Device device = deviceRepository.findById(skillPayload.getDevice()).orElse(skill.getDevice());
        Failure failure = failureRepository.findById(skillPayload.getFailure()).orElse(skill.getFailure());
        skillRepository.save(new Skill(skill, skillPayload, device, failure, serviceTechnicianService.idsToServiceTechnician(skillPayload.getServiceTechnicians())));
        return ResponseEntity.ok().build();
    }

    public Set<Skill> idsToSkills(Set<Long> skillsIds) {
        Set<Skill> skills = new HashSet<>();
        if (skillsIds != null) {
            for (Long skillId : skillsIds) {
                Skill skill = skillRepository.findById(skillId).orElse(null);
                if (skill != null) skills.add(skill);
            }
        }
        return skills;
    }
}
