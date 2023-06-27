package org.indritbreti.restprocessordemo.API.DemoEntity;;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.Filter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.FullTextSearchFilter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.LiteralFunctionArg;
import org.indritbreti.restprocessor.DynamicRESTController.CriteriaParameters;
import org.indritbreti.restprocessordemo.API.common.BaseService;







import org.indritbreti.restprocessordemo.API.DemoEntity.category.Category;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.CategoryService;
import org.indritbreti.restprocessordemo.API.common.BaseService;
import org.indritbreti.restprocessordemo.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService extends BaseService<Product> {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    @Autowired
    public ProductService(ProductRepository ProductRepository, CategoryService categoryService) {
        super(ProductRepository, "Product");
        this.productRepository = ProductRepository;
        this.categoryService = categoryService;
    }

    public Collection<Product> getAllByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    public Page<Product> getAllByCriteria(boolean isVisibleRequired, String searchQuery, CriteriaParameters cp) {
        if (isVisibleRequired)
            cp.addFilter(new Filter<>("visible", CriteriaOperator.EQUAL, true));
        if (Utilities.notNullOrEmpty(searchQuery)) {
            cp.addFilter(new FullTextSearchFilter(searchQuery));
            cp.addSortByFunctionArg("searchBestMatch", new LiteralFunctionArg(0, searchQuery));
        }
        return productRepository.findAllByCriteria(cp);
    }

    public List<Product> addAllProducts(ArrayList<Product> products) {
        return productRepository.saveAll(products);
    }


    public Product setCustomFields(Long id, Map<String, Object> customFields) {
        Product product = getById(id);
        product.setCustomFields(customFields);
        return save(product);
    }

    public Product setCustomFields(Long id, List<Map.Entry<String, Object>> customFields) {
        Map<String, Object> customFields_ = new HashMap<>();
        for (Map.Entry<String, Object> entry : customFields)
            customFields_.put(entry.getKey(), entry.getValue());
        return setCustomFields(id, customFields_);
    }

    public Product addCustomFields(Long id, Map<String, Object> customFields) {
        Product product = getById(id);
        product.addCustomFields(customFields);
        return save(product);
    }

    public Product removeCustomField(Long id, String key) {
        Product product = getById(id);
        product.removeCustomField(key);
        return save(product);
    }

    public Product removeCustomFields(Long id, Set<String> keys) {
        Product product = getById(id);
        product.removeCustomFields(keys);
        return save(product);
    }

    public Product setCategories(Product product, List<Long> categoriesIds) {
        return setCategories(product, categoriesIds, true);
    }

    public Product setCategories(Product product, List<Long> categoriesIds, boolean persist) {
        List<Category> categories = categoryService.getCategories(categoriesIds);
        product.setCategories(categories);
        if (persist)
            return save(product);
        return product;
    }

    public void addCategory(Long productId, Long categoryId) {
        Category category = categoryService.getById(categoryId);
        Product product = productRepository.findProductById(productId);
        product.addCategory(category);
        productRepository.save(product);
    }

    public Product addCategories(Product product, List<Long> categoryIds) {
        return addCategories(product, categoryIds, true);
    }

    public Product addCategories(Product product, List<Long> categoryIds, boolean persist) {
        if (categoryIds != null) {
            for (Long id : categoryIds)
                product.addCategory(categoryService.getById(id));
        }
        if (persist)
            return productRepository.save(product);
        return product;
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }
}
