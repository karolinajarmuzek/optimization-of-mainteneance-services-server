package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Customer;
import com.oms.serverapp.payload.CustomerPayload;
import com.oms.serverapp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/customer")
@RestController
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerPayload>  retrieveAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping(path = "{id}")
    public CustomerPayload retrieveCustomerById(@PathVariable("id") Long id) throws NotFoundException {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerPayload customerPayload) {
        return customerService.addCustomer(customerPayload);
    }

    @DeleteMapping(path = "{id}")
    public void deleteCustomerById(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateCustomerById(@RequestBody CustomerPayload customerPayload, @PathVariable("id") Long id) {
        return customerService.updateCustomer(customerPayload, id);
    }
}
