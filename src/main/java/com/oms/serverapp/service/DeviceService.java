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
    private SkillRepository skillRepository;
    private ReportRepository reportRepository;
    private ServiceHelpers serviceHelpers = new ServiceHelpers();

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, SkillRepository skillRepository, ReportRepository reportRepository) {
        this.deviceRepository = deviceRepository;
        this.skillRepository = skillRepository;
        this.reportRepository = reportRepository;
    }

    public List<DevicePayload> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();
        List<DevicePayload> devicesResponse = new ArrayList<>();
        for (Device device: devices) {
            devicesResponse.add(new DevicePayload(device.getId(), device.getName(), device.getType(), serviceHelpers.skillsToIds(device.getSkills()), serviceHelpers.reportsToIds(device.getReports())));
        }
        return devicesResponse;
    }

    public DevicePayload getDeviceById(Long id) throws NotFoundException {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new NotFoundException(String.format("Device with id = %d not found.", id));
        }
        return new DevicePayload(device.getId(), device.getName(), device.getType(), serviceHelpers.skillsToIds(device.getSkills()), serviceHelpers.reportsToIds(device.getReports()));
    }

    public ResponseEntity<Device> addDevice(DevicePayload devicePayload) {
        Device savedDevice = deviceRepository.save(new Device(devicePayload.getName(), devicePayload.getType(), serviceHelpers.idsToSkills(devicePayload.getSkills()), serviceHelpers.idsToReports(devicePayload.getReports())));
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
        deviceRepository.save(new Device(device.getId(), devicePayload.getName(), devicePayload.getType(), serviceHelpers.idsToSkills(devicePayload.getSkills()), serviceHelpers.idsToReports(devicePayload.getReports())));
        return ResponseEntity.ok().build();
    }
}
