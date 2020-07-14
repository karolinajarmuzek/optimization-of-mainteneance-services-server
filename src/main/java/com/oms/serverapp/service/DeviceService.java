package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Device;
import com.oms.serverapp.payload.DevicePayload;
import com.oms.serverapp.repository.DeviceRepository;
import com.oms.serverapp.repository.ReportRepository;
import com.oms.serverapp.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class DeviceService {

    private DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<DevicePayload> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();
        List<DevicePayload> devicesResponse = new ArrayList<>();
        for (Device device: devices) {
            devicesResponse.add(new DevicePayload(device));
        }
        return devicesResponse;
    }

    public DevicePayload getDeviceById(Long id) throws NotFoundException {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new NotFoundException(String.format("Device with id = %d not found.", id));
        }
        return new DevicePayload(device);
    }

    public ResponseEntity<Device> addDevice(DevicePayload devicePayload) {
        Device savedDevice = deviceRepository.save(new Device(devicePayload));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedDevice.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateDevice(DevicePayload devicePayload, Long id) {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            return ResponseEntity.notFound().build();
        }
        deviceRepository.save(new Device(device, devicePayload));
        return ResponseEntity.ok().build();
    }
}
