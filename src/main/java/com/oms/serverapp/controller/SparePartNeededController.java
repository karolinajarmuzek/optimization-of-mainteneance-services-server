package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.SparePartNeeded;
import com.oms.serverapp.payload.SparePartNeededPayload;
import com.oms.serverapp.service.SparePartNeededService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(path = "api/sparepartneeded")
@RestController
public class SparePartNeededController {

    private final SparePartNeededService sparePartNeededService;

    @Autowired
    public SparePartNeededController(SparePartNeededService sparePartNeededService) {
        this.sparePartNeededService = sparePartNeededService;
    }

    @GetMapping
    public List<SparePartNeededPayload> retrieveAllSparePartsNeeded() {
        return sparePartNeededService.getAllSparePartsNeeded();
    }

    @GetMapping(path = "{id}")
    public SparePartNeededPayload retrieveSparePartNeededById(@PathVariable("id") Long id) throws NotFoundException {
        return sparePartNeededService.getSparePartNeededById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<SparePartNeeded> createSparePartNeeded(@Valid @RequestBody SparePartNeededPayload sparePartNeededPayload) {
        return sparePartNeededService.addSparePartNeeded(sparePartNeededPayload);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(path = "{id}")
    public void deleteSparePartNeededById(@PathVariable("id") Long id) {
        sparePartNeededService.deleteSparePartNeeded(id);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateSparePartNeeded(@RequestBody SparePartNeededPayload sparePartNeededPayload, @PathVariable("id") Long id) {
        return sparePartNeededService.updateSparePartNeeded(sparePartNeededPayload, id);
    }
}
