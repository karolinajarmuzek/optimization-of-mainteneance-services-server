package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Failure;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.repository.FailureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class FailureService {

    private FailureRepository failureRepository;
    private ServiceHelpers serviceHelpers = new ServiceHelpers();

    @Autowired
    public FailureService(FailureRepository failureRepository) {
        this.failureRepository = failureRepository;
    }

    public List<FailurePayload> getAllFailures() {
        List<Failure> failures = failureRepository.findAll();
        List<FailurePayload> failuresResponse = new ArrayList<>();
        for (Failure failure: failures) {
            failuresResponse.add(new FailurePayload(failure.getId(), failure.getType(), serviceHelpers.skillsToIds(failure.getSkills()), serviceHelpers.reportsToIds(failure.getReports())));
        }
        return failuresResponse;
    }

    public FailurePayload getFailureById(Long id) throws NotFoundException {
        Failure failure = failureRepository.findById(id).orElse(null);
        if (failure == null) {
            throw new NotFoundException(String.format("Failure with id = %d not found.", id));
        }
        return new FailurePayload(failure.getId(), failure.getType(), serviceHelpers.skillsToIds(failure.getSkills()), serviceHelpers.reportsToIds(failure.getReports()));
    }

    public ResponseEntity<Failure> addFailure(FailurePayload failurePayload) {
        Failure savedFailure = failureRepository.save(new Failure(failurePayload.getType(), serviceHelpers.idsToSkills(failurePayload.getSkills()), serviceHelpers.idsToReports(failurePayload.getReports())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedFailure.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteFailure(Long id) {
        failureRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateFailure(FailurePayload failurePayload, Long id) {
        Failure failure = failureRepository.findById(id).orElse(null);
        if (failure == null) {
            return ResponseEntity.notFound().build();
        }
        failureRepository.save(new Failure(failure.getId(), failurePayload.getType(), serviceHelpers.idsToSkills(failurePayload.getSkills()), serviceHelpers.idsToReports(failurePayload.getReports())));
        return ResponseEntity.ok().build();
    }
}
