package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Customer;
import com.oms.serverapp.model.Device;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceById(Long id) throws NotFoundException {
        Optional<Device> device = deviceRepository.findById(id);

        if (!device.isPresent()) {
            throw new NotFoundException(String.format("Device with id = %d not found.", id));
        }

        return device.get();
    }

    public ResponseEntity<Device> addDevice(Device device) {
        Device savedDevice = deviceRepository.save(device);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedDevice.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateDevice(Device device, Long id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);

        if (!deviceOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        device.setId(id);
        deviceRepository.save(device);

        return ResponseEntity.ok().build();
    }
}
