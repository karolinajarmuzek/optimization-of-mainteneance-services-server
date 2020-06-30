package com.oms.serverapp.service;

import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.model.ServiceMan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServiceManService {

    private ServiceManRepository serviceManRepository;

    @Autowired
    public ServiceManService(ServiceManRepository serviceManRepository) {
        this.serviceManRepository = serviceManRepository;
    }

    public List<ServiceMan> getAllServiceMan() {
        return serviceManRepository.findAll();
    }

    public ServiceMan getServiceManById(Long id) {
        return serviceManRepository.findById(id).orElse(null);
    }

    public ServiceMan addServiceMan(ServiceMan serviceMan) {
        return serviceManRepository.save(serviceMan);
    }

    public void deleteServiceMan(Long id) {
        serviceManRepository.deleteById(id);
    }

    public void updateServiceMan(ServiceMan serviceMan) {
        serviceManRepository.save(serviceMan);
    }
}
