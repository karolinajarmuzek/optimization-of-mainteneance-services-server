package com.oms.serverapp.payload;
import com.oms.serverapp.model.SparePartNeeded;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SparePartNeededPayload {

    private Long id;

    @NotNull(message = "Skill cannot be null.")
    private Long skill;

    @NotNull(message = "Sparepart cannot be null.")
    private Long sparePart;

    @NotNull(message = "Quantity cannot be null.")
    @Min(1)
    private Integer quantity;

    public SparePartNeededPayload() {};

    public SparePartNeededPayload(SparePartNeeded sparePartNeeded) {
        this.id = sparePartNeeded.getId();
        this.skill = sparePartNeeded.getSkill().getId();
        this.sparePart = sparePartNeeded.getSparePart().getId();
        this.quantity = sparePartNeeded.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkill() {
        return skill;
    }

    public void setSkill(Long skill) {
        this.skill = skill;
    }

    public Long getSparePart() {
        return sparePart;
    }

    public void setSparePart(Long sparePart) {
        this.sparePart = sparePart;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
