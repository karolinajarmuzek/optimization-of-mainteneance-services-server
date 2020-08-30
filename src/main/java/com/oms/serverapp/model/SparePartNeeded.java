package com.oms.serverapp.model;

import com.oms.serverapp.payload.SkillPayload;
import com.oms.serverapp.payload.SparePartNeededPayload;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sparepartneeded")
public class SparePartNeeded {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Skill cannot be null.")
    @ManyToOne(fetch = FetchType.LAZY)
    private Skill skill;

    @NotNull(message = "Sparepart cannot be null.")
    @ManyToOne(fetch = FetchType.LAZY)
    private SparePart sparePart;

    @NotNull(message = "Quantity cannot be null.")
    @Min(1)
    private Integer quantity;

    public SparePartNeeded() {
    }

    public SparePartNeeded(SparePartNeededPayload sparePartNeededPayload, Skill skill, SparePart sparePart) {
        this.skill = skill;
        this.sparePart = sparePart;
        this.quantity = sparePartNeededPayload.getQuantity();
    }

    public SparePartNeeded(SparePartNeeded sparePartNeeded, SparePartNeededPayload sparePartNeededPayload, Skill skill, SparePart sparePart) {
        this.id = sparePartNeeded.getId();
        this.skill = skill;
        this.sparePart = sparePart;
        this.quantity = sparePartNeededPayload.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public SparePart getSparePart() {
        return sparePart;
    }

    public void setSparePart(SparePart sparePart) {
        this.sparePart = sparePart;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
