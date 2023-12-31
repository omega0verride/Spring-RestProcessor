package org.indritbreti.restprocessordemo.API.DemoEntity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class ProductBase {

    @NotNull
    private String title;

    @Column(length = 1024)
    private String description;

    @NotNull
    @Min(0)
    @Digits(fraction = 0, integer = 9)
    private Double price;

    @Min(0)
    private double range;

    private Integer discount = 0; // percentage

    private boolean used = false; // if set to true the product is marked as "used" otherwise it is "new"

    @Transient
    public Double getCalculatedPrice() {
        return price + (price * discount);
    }

    public ProductBase(String title, String description, Double price, Integer discount, boolean used, double range) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.used = used;
        this.range = range;
    }
}
