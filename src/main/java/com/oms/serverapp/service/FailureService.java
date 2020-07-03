package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Failure;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.repository.FailureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class FailureService {

    private FailureRepository failureRepository;

    @Autowired
    public FailureService(FailureRepository failureRepository) {
        this.failureRepository = failureRepository;
    }

    public List<Failure> getAllFailures() {
        return failureRepository.findAll();
    }

    public Failure getFailureById(Long id) throws NotFoundException {
        Optional<Failure> failure = failureRepository.findById(id);

        if (!failure.isPresent()) {
            throw new NotFoundException(String.format("Failure with id = %d not found.", id));
        }

        return failure.get();
    }

    public ResponseEntity<Failure> addFailure(Failure failure) {
        Failure savedFailure = failureRepository.save(failure);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedFailure.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteFailure(Long id) {
        failureRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateFailure(Failure failure, Long id) {
        Optional<Failure> failureOptional = failureRepository.findById(id);

        if (!failureOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        failure.setId(id);
        failureRepository.save(failure);

        return ResponseEntity.ok().build();
    }
}
