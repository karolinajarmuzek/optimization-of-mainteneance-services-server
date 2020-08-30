package com.oms.serverapp.model;

import com.oms.serverapp.payload.SparePartPayload;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "spareparts", uniqueConstraints = {})
public class SparePart {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must be between 3 and 15 characters.")
    @Size(min = 3, max = 15, message = "Name must be between 3 and 15 characters.")
    private String name;

    @NotNull(message = "Quantity cannot be null.")
    @Min(0)
    private int quantity;

    @NotNull(message = "Price cannot be null.")
    @Min(0)
    private int price;

    @OneToMany(mappedBy = "sparePart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<SparePartNeeded> sparePartsNeeded;

    public SparePart() {
    }

    public SparePart(SparePartPayload sparePartPayload) {
        this.name = sparePartPayload.getName();
        this.quantity = sparePartPayload.getQuantity();
        this.price = sparePartPayload.getPrice();
    }

    public SparePart(SparePart sparePart, SparePartPayload sparePartPayload) {
        this.id = sparePart.getId();
        this.name = sparePartPayload.getName();
        this.quantity = sparePartPayload.getQuantity();
        this.price = sparePartPayload.getPrice();
        this.sparePartsNeeded = sparePart.getSparePartsNeeded();
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Set<SparePartNeeded> getSparePartsNeeded() {
        return sparePartsNeeded;
    }

    public void setSparePartsNeeded(Set<SparePartNeeded> sparePartsNeeded) {
        this.sparePartsNeeded = sparePartsNeeded;
    }
}
