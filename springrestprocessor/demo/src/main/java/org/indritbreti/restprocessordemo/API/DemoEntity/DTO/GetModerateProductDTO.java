package org.indritbreti.restprocessordemo.API.DemoEntity.DTO;

import org.indritbreti.restprocessordemo.API.common.AuditBaseDTO;

import lombok.Getter;
import lombok.Setter;
import org.indritbreti.restprocessordemo.API.DemoEntity.Product;
import org.indritbreti.restprocessordemo.API.common.AuditBaseDTO;

@Getter
@Setter
public class GetModerateProductDTO extends AuditBaseDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Double range;
    private int discount;
    private boolean used;
    private int stock;
    private String thumbnail;
    private String instagramPostURL;
    private String facebookPostURL;

    public GetModerateProductDTO(Product p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.price = p.getPrice();
        this.range = p.getRange();
        this.discount = p.getDiscount();
        this.used = p.isUsed();
        this.stock = p.getStock();
        this.instagramPostURL = p.getInstagramPostURL();
        this.facebookPostURL = p.getFacebookPostURL();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();
    }
}
