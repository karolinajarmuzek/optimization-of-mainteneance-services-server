package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    private SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill getSkillById(Long id) throws NotFoundException {
        Optional<Skill> skill = skillRepository.findById(id);

        if (!skill.isPresent()) {
            throw new NotFoundException(String.format("Skill with id = %d not found.", id));
        }

        return skill.get();
    }

    public ResponseEntity<Skill> addSkill(Skill skill) {
        Skill savedSkill = skillRepository.save(skill);
        // get
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedSkill).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateSkill(Skill skill, Long id) {
        Optional<Skill> skillOptional = skillRepository.findById(id);

        if (!skillOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        //skill.setId(id);
        skillRepository.save(skill);

        return ResponseEntity.noContent().build();
    }
}
