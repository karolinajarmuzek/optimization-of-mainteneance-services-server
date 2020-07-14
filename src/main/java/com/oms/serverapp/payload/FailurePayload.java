package com.oms.serverapp.payload;

import com.oms.serverapp.model.Failure;

import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.stream.Collectors;

public class FailurePayload {

    private Long id;

    @NotBlank(message = "Type cannot be blank.")
    private String type;

    public FailurePayload() {
    }

    public FailurePayload(Failure failure) {
        this.id = failure.getId();
        this.type = failure.getType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
