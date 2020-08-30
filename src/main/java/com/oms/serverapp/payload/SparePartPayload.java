package com.oms.serverapp.payload;

import com.oms.serverapp.model.SparePart;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SparePartPayload {

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

    public SparePartPayload(SparePart sparePart) {
        this.id = sparePart.getId();
        this.name = sparePart.getName();
        this.quantity = sparePart.getQuantity();
        this.price = sparePart.getPrice();
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
}
