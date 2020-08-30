package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Failure;
import com.oms.serverapp.model.SparePart;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.payload.SparePartPayload;
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
public class SparePartService {

    private SparePartRepository sparePartRepository;

    @Autowired
    public SparePartService(SparePartRepository sparePartRepository) {
        this.sparePartRepository = sparePartRepository;
    }

    public List<SparePartPayload> getAllSpareParts() {
        List<SparePart> spareParts = sparePartRepository.findAll();
        List<SparePartPayload> sparePartsResponse = new ArrayList<>();
        for (SparePart sparePart: spareParts) {
            sparePartsResponse.add(new SparePartPayload(sparePart) );
        }
        return sparePartsResponse;
    }

    public SparePartPayload getSparePartById(Long id) throws NotFoundException {
        SparePart sparePart = sparePartRepository.findById(id).orElse(null);
        if (sparePart == null) {
            throw new NotFoundException(String.format("SparePart with id = %d not found.", id));
        }
        return new SparePartPayload(sparePart);
    }

    public ResponseEntity<SparePart> addSparePart(SparePartPayload sparePartPayload) {
        SparePart savedSparePart = sparePartRepository.save(new SparePart(sparePartPayload));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedSparePart.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteSparePart(Long id) { sparePartRepository.deleteById(id);}

    public ResponseEntity<Object> updateSparePart(SparePartPayload sparePartPayload, Long id) {
        SparePart sparePart = sparePartRepository.findById(id).orElse(null);
        if (sparePart == null) {
            return ResponseEntity.notFound().build();
        }
        sparePartRepository.save(new SparePart(sparePart, sparePartPayload));
        return ResponseEntity.ok().build();
    }


}
