package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.model.SparePart;
import com.oms.serverapp.model.SparePartNeeded;
import com.oms.serverapp.payload.SparePartNeededPayload;
import com.oms.serverapp.repository.SkillRepository;
import com.oms.serverapp.repository.SparePartNeededRepository;
import com.oms.serverapp.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SparePartNeededService {

    private SparePartNeededRepository sparePartNeededRepository;
    private SkillRepository skillRepository;
    private SparePartRepository sparePartRepository;

    @Autowired
    public SparePartNeededService(SparePartNeededRepository sparePartNeededRepository, SkillRepository skillRepository, SparePartRepository sparePartRepository) {
        this.sparePartNeededRepository = sparePartNeededRepository;
        this.skillRepository = skillRepository;
        this.sparePartRepository = sparePartRepository;
    }

    public List<SparePartNeededPayload> getAllSparePartsNeeded() {
        List<SparePartNeeded> sparePartNeededs = sparePartNeededRepository.findAll();
        List<SparePartNeededPayload> sparePartsNeededResponse = new ArrayList<>();
        for (SparePartNeeded sparePartNeeded : sparePartNeededs) {
            sparePartsNeededResponse.add(new SparePartNeededPayload(sparePartNeeded));
        }
        return sparePartsNeededResponse;
    }

    public SparePartNeededPayload getSparePartNeededById(Long id) throws NotFoundException {
        SparePartNeeded sparePartNeeded = sparePartNeededRepository.findById(id).orElse(null);
        if (sparePartNeeded == null) {
            throw new NotFoundException(String.format("SparePartNeeded with id = %d not found.", id));
        }
        return new SparePartNeededPayload(sparePartNeeded);
    }

    public ResponseEntity<SparePartNeeded> addSparePartNeeded(SparePartNeededPayload sparePartNeededPayload) {
        Skill skill = skillRepository.findById(sparePartNeededPayload.getSkill()).orElse(null);
        SparePart sparePart = sparePartRepository.findById(sparePartNeededPayload.getSparePart()).orElse(null);
        SparePartNeeded savedSparePartNeeded = sparePartNeededRepository.save(new SparePartNeeded(sparePartNeededPayload, skill, sparePart));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedSparePartNeeded.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteSparePartNeeded(Long id) {
        sparePartNeededRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateSparePartNeeded(SparePartNeededPayload sparePartNeededPayload, Long id) {
        SparePartNeeded sparePartNeeded = sparePartNeededRepository.findById(id).orElse(null);
        if (sparePartNeeded == null) {
            return ResponseEntity.notFound().build();
        }
        Skill skill = skillRepository.findById(sparePartNeededPayload.getSkill()).orElse(null);
        SparePart sparePart = sparePartRepository.findById(sparePartNeededPayload.getSparePart()).orElse(null);
        sparePartNeededRepository.save(new SparePartNeeded(sparePartNeeded, sparePartNeededPayload, skill, sparePart));
        return ResponseEntity.ok().build();
    }
}
