package com.oms.serverapp.controller;

import com.oms.serverapp.exception.IncorrectRepairTimeException;
import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.payload.SkillPayload;
import com.oms.serverapp.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/skill")
@RestController
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<SkillPayload> retrieveAllSkills() {
        return skillService.getAllSkills();
    }

    @GetMapping(path = "{id}")
    public SkillPayload retrieveSkillById(@PathVariable("id") Long id) throws NotFoundException {
        return skillService.getSkillById(id);
    }

    @PostMapping
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody SkillPayload skillPayload) throws IncorrectRepairTimeException {
        return skillService.addSkill(skillPayload);
    }

    @DeleteMapping(path = "{id}")
    public void deleteSkillById(@PathVariable("id") Long id) {
        skillService.deleteSkill(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateSkillById(@Valid @RequestBody SkillPayload skillPayload, @PathVariable("id") Long id) {
        return skillService.updateSkill(skillPayload, id);
    }
}
