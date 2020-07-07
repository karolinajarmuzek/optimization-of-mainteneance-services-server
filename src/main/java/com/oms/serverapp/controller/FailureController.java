package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Failure;
import com.oms.serverapp.payload.FailurePayload;
import com.oms.serverapp.service.FailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/failure")
@RestController
public class FailureController {

    private final FailureService failureService;

    @Autowired
    public FailureController(FailureService failureService) {
        this.failureService = failureService;
    }

    @GetMapping
    public List<FailurePayload> retrieveAllFailures() {
        return failureService.getAllFailures();
    }

    @GetMapping(path = "{id}")
    public FailurePayload retrieveFailureById(@PathVariable("id") Long id) throws NotFoundException {
        return failureService.getFailureById(id);
    }

    @PostMapping
    public ResponseEntity<Failure> createFailure(@Valid @RequestBody FailurePayload failurePayload) {
        return failureService.addFailure(failurePayload);
    }

    @DeleteMapping(path = "{id}")
    public void deleteFailureById(@PathVariable Long id) {
        failureService.deleteFailure(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateFailureById(@Valid @RequestBody FailurePayload failurePayload, @PathVariable("id") Long id) {
        return failureService.updateFailure(failurePayload, id);
    }
}
