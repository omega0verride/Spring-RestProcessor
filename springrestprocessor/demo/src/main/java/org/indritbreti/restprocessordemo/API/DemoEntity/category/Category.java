package org.indritbreti.restprocessordemo.API.DemoEntity.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;



import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.CreateCategoryDTO;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.indritbreti.restprocessordemo.API.DemoEntity.Product;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.CreateCategoryDTO;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.GetCategoryDTO;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.GetMinimalCategoryDTO;
import org.indritbreti.restprocessordemo.API.common.AuditData;
import org.indritbreti.restprocessordemo.API.common.Auditable;

import java.util.ArrayList;
import java.util.List;


@Entity(name = "categories")
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Auditable {

    @Embedded
    AuditData auditData = new AuditData();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id")
    private Long id;

    private boolean visible = true;

    @Column(name = "name", length = 50, nullable = false)
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(unique = true, nullable = false)
    private String nameLowerCased;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST},
            mappedBy = "categories")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    public Category(String name) {
        setName(name);
    }

    public Category(CreateCategoryDTO createCategoryDTO) {
        // no need to check for null values, these are all mandatory fields, the DTO will be marked invalid otherwise
        this(createCategoryDTO.getName());
        if (createCategoryDTO.getVisible() != null) setVisible(createCategoryDTO.getVisible());
    }

    @PrePersist
    @PreUpdate
    private void prepare() {
        this.nameLowerCased = name == null ? null : name.toLowerCase();
    }

    public static GetCategoryDTO toGetCategoryDTO(Category category) {
        return new GetCategoryDTO(category);
    }

    public static GetMinimalCategoryDTO toGetMinimalCategoryDTO(Category category) {
        return new GetMinimalCategoryDTO(category);
    }

    public GetCategoryDTO toGetCategoryDTO() {
        return new GetCategoryDTO(this);
    }

    public GetMinimalCategoryDTO toGetMinimalCategoryDTO() {
        return new GetMinimalCategoryDTO(this);
    }

    @Override
    public long getCreatedAt() {
        return auditData.getCreatedAt();
    }

    @Override
    public long getUpdatedAt() {
        return auditData.getUpdatedAt();
    }
}
