package org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryDTO {
    private Boolean visible;

    @NotBlank(message = "Category name cannot be blank!")
    @Size(min = 1, message = "Category name too short!")
    @Size(max = 50, message = "Category name too long!")
    private String name;
}
