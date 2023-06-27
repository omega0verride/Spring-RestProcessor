package org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO;;
import org.indritbreti.restprocessordemo.API.common.AuditBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.Category;
import org.indritbreti.restprocessordemo.API.common.AuditBaseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryDTO extends AuditBaseDTO {
    private Long id;
    private boolean visible;
    private String name;

    public GetCategoryDTO(Category category) {
        this.id = category.getId();
        this.visible = category.isVisible();
        this.name = category.getName();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }

    public static GetCategoryDTO fromCategory(Category category) {
        return new GetCategoryDTO(category);
    }
}
