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
    private ReportService reportService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, ReportService reportService) {
        this.customerRepository = customerRepository;
        this.reportService = reportService;
    }

    public List<CustomerPayload> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerPayload> customersResponse = new ArrayList<>();
        for (Customer customer: customers) {
            customersResponse.add(new CustomerPayload(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getPhoneNumber(), reportService.reportsToIds(customer.getReports())));
        }
        return customersResponse;
    }

    public CustomerPayload getCustomerById(Long id) throws NotFoundException {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            throw new NotFoundException(String.format("Customer with id = %d not found.", id));
        }
        return new CustomerPayload(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getPhoneNumber(), reportService.reportsToIds(customer.getReports()));
    }

    public ResponseEntity<Customer> addCustomer(CustomerPayload customerPayload) {
        Customer savedCustomer = customerRepository.save(new Customer(customerPayload.getFirstName(), customerPayload.getLastName(), customerPayload.getPhoneNumber(), reportService.idsToReports(customerPayload.getReports())));
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
        customerRepository.save(new Customer(customer.getId(), customerPayload.getFirstName(), customerPayload.getLastName(), customerPayload.getPhoneNumber(), reportService.idsToReports(customerPayload.getReports())));
        return ResponseEntity.ok().build();
    }

}
