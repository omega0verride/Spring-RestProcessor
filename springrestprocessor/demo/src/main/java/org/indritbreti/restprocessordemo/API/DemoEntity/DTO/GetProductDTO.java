package org.indritbreti.restprocessordemo.API.DemoEntity.DTO;;
import org.indritbreti.restprocessordemo.API.DemoEntity.Product;
import lombok.Getter;
import lombok.Setter;
import org.indritbreti.restprocessordemo.API.DemoEntity.Product;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.GetMinimalCategoryDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class GetProductDTO extends GetModerateProductDTO {
    boolean visible;

    private Map<String, Object> customFields;
    private List<GetMinimalCategoryDTO> categories;

    public GetProductDTO(Product p) {
        super(p);
        this.visible = p.isVisible();
        this.customFields = p.getCustomFields();
        this.categories = p.getCategories().stream().map(c -> c.toGetMinimalCategoryDTO()).collect(Collectors.toList());
    }
}
