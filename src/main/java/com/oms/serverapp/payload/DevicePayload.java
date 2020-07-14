package com.oms.serverapp.payload;

import com.oms.serverapp.model.Device;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

public class DevicePayload {

    private Long id;

    @NotBlank()
    @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters.")
    private String name;

    @NotBlank()
    @Size(min = 3, max = 30, message = "Type must be between 3 and 30 characters.")
    private String type;

    public DevicePayload() {
    }

    public DevicePayload(Device device) {
        this.id = device.getId();
        this.name = device.getName();
        this.type = device.getType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
