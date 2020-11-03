package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.SparePart;
import com.oms.serverapp.payload.SparePartPayload;
import com.oms.serverapp.service.SparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/sparepart")
@RestController
public class SparePartController {

    private final SparePartService sparePartService;

    @Autowired
    public SparePartController(SparePartService sparePartService) {
        this.sparePartService = sparePartService;
    }

    @GetMapping
    public List<SparePartPayload> retrieveAllSpareParts() {
        return sparePartService.getAllSpareParts();
    }

    @GetMapping(path = "{id}")
    public SparePartPayload retrieveSparePartById(@PathVariable("id") Long id) throws NotFoundException {
        return sparePartService.getSparePartById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<SparePart> createSparePart(@Valid @RequestBody SparePartPayload sparePartPayload) {
        return sparePartService.addSparePart(sparePartPayload);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(path = "{id}")
    public void deleteSparePartById(@PathVariable("id") Long id) {
        sparePartService.deleteSparePart(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateSparePart(@RequestBody SparePartPayload sparePartPayload, @PathVariable("id") Long id) {
        return sparePartService.updateSparePart(sparePartPayload, id);
    }
}
