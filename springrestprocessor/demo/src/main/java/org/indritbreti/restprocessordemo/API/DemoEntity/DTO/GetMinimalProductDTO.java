package org.indritbreti.restprocessordemo.API.DemoEntity.DTO;;
import org.indritbreti.restprocessordemo.API.DemoEntity.ProductService;
import lombok.Getter;
import lombok.Setter;
import org.indritbreti.restprocessordemo.API.DemoEntity.Product;
import org.indritbreti.restprocessordemo.API.DemoEntity.ProductService;

@Getter
@Setter
public class GetMinimalProductDTO {
    private Long id;
    private String title;
    private Double price;
    private Double range;
    private String thumbnail;

    public GetMinimalProductDTO(Product p, ProductService productService) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.price = p.getPrice();
        this.range = p.getRange();
    }
}
