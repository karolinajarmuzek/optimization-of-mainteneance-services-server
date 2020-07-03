package com.oms.serverapp.service;

import com.oms.serverapp.model.Customer;
import com.oms.serverapp.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);

        //

        return customer.get();
    }

    public ResponseEntity<Customer> addCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedCustomer.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateCustomer(Customer customer, Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (!customerOptional.isPresent()) {
            return  ResponseEntity.notFound().build();
        }

        customer.setId(id);
        customerRepository.save(customer);

        return ResponseEntity.ok().build();
    }

}
