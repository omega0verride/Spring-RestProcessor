package org.indritbreti.restprocessordemo.API.DemoEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.Category;


import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.Category;
import org.indritbreti.restprocessordemo.API.common.AuditData;
import org.indritbreti.restprocessordemo.API.common.Auditable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Entity(name = "products")
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@Repository
public class Product extends ProductBase implements Auditable {
    @Embedded
    AuditData auditData = new AuditData();

    @Id
    @SequenceGenerator(name = "product_sequence", sequenceName = "product_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_sequence")
    @Column(name = "product_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @EqualsAndHashCode.Include // limit the hash generator to PK
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(length = 2048)
    private String instagramPostURL;
    @Column(length = 2048)
    private String facebookPostURL;
    @JsonIgnore
    @Column(length = 2048)
    private String thumbnailFilename;

    private boolean visible = true;

    private Integer stock = 0;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> customFields = new HashMap<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "product_categories",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id",
                    foreignKey = @ForeignKey(
                            name = "FK_category_id",
                            foreignKeyDefinition = "FOREIGN KEY (category_id) REFERENCES categories(category_id) ON UPDATE CASCADE ON DELETE CASCADE"
                    ))})
    private List<Category> categories = new ArrayList<>();

    public Product(String title, String description, int stock, Double price, int discount, double range, String instagramPostURL, String facebookPostURL) {
        setTitle(title);
        setDescription(description);
        setStock(stock);
        setPrice(price);
        setDiscount(discount);
        setRange(range);
    }

    public Product(String title, String description, int stock, Double price, int discount, Double range) {
        this(title, description, stock, price, discount, range, null, null);
    }

    public void addCustomFields(Map<String, Object> customFields) {
        if (this.customFields == null)
            this.customFields = new HashMap<>();
        if (customFields != null)
            this.customFields.putAll(customFields);
    }

    public void removeCustomField(String key) {
        if (this.customFields == null)
            return;
        if (key != null)
            this.customFields.remove(key);
    }

    public void removeCustomFields(Set<String> keys) {
        if (this.customFields == null)
            return;
        for (String key : keys)
            this.customFields.remove(key);
    }

    public void addCategory(Category category) {
        this.categories.add(category); // make sure to pass object instead of referencing self to avoid a concurrency issue
    }

    public void removeCategory(long categoryId) {
        Category category = this.categories.stream().filter(t -> t.getId() == categoryId).findFirst().orElse(null);
        if (category != null) {
            this.categories.remove(category);
            category.getProducts().remove(this);
        }
    }

    public boolean isVisible() {
        if (visible)
            return true;
        return categories.size() == 0 || categories.stream().anyMatch(Category::isVisible);
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
