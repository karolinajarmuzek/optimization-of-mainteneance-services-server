package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Customer;
import com.oms.serverapp.payload.CustomerPayload;
import com.oms.serverapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerPayload> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerPayload> customersResponse = new ArrayList<>();
        for (Customer customer: customers) {
            customersResponse.add(new CustomerPayload(customer));
        }
        return customersResponse;
    }

    public CustomerPayload getCustomerById(Long id) throws NotFoundException {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            throw new NotFoundException(String.format("Customer with id = %d not found.", id));
        }
        return new CustomerPayload(customer);
    }

    public ResponseEntity<Customer> addCustomer(CustomerPayload customerPayload) {
        Customer savedCustomer = customerRepository.save(new Customer(customerPayload));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedCustomer.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateCustomer(CustomerPayload customerPayload, Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return  ResponseEntity.notFound().build();
        }
        customerRepository.save(new Customer(customer, customerPayload));
        return ResponseEntity.ok().build();
    }

}
