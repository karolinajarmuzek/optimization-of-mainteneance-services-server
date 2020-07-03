package com.oms.serverapp.controller;

import com.oms.serverapp.model.Device;
import com.oms.serverapp.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/device")
@RestController
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public List<Device> retrieveAllDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping(path = "{id}")
    public Device retrieveDeviceById(@PathVariable("id") Long id) {
        return deviceService.getDeviceById(id);
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody Device device) {
        return deviceService.addDevice(device);
    }

    @DeleteMapping(path = "{id}")
    public void deleteDeviceById(@PathVariable("id") Long id) {
        deviceService.deleteDevice(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateDeviceById(@Valid @RequestBody Device device, @PathVariable("id") Long id) {
        return deviceService.updateDevice(device, id);
    }
}
