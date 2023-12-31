package org.indritbreti.restprocessordemo.API.DemoEntity.category;;


import org.indritbreti.restprocessordemo.API.common.BaseService;

import org.indritbreti.restprocessordemo.API.DemoEntity.ProductRepository;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.CreateCategoryDTO;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.UpdateCategoryDTO;
import org.indritbreti.restprocessordemo.API.common.BaseService;
import org.indritbreti.restprocessordemo.exceptions.api.ResourceAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends BaseService<Category> {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository IProductRepository) {
        super(categoryRepository, "Category");
        this.categoryRepository = categoryRepository;
    }

    public Category getByName(String name, boolean caseSensitive) {
        if (caseSensitive)
            return categoryRepository.findCategoryByName(name).orElseThrow(() -> {
                throw buildResourceNotFoundException("name(case-sensitive)", name);
            });
        return categoryRepository.findCategoryByNameLowerCased(name.toLowerCase()).orElseThrow(() -> {
            throw buildResourceNotFoundException("name(case-insensitive)", name);
        });
    }

    public boolean existsByName(String name, boolean caseSensitive) {
        if (caseSensitive)
            return categoryRepository.existsByName(name);
        return categoryRepository.existsByNameLowerCased(name.toLowerCase());
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoriesIfVisible() {
        return categoryRepository.findAllByVisibleTrue();
    }

    public List<Category> getCategories(List<Long> categoriesIds) {
        return categoryRepository.findAllById(categoriesIds);
    }

    public Category create(CreateCategoryDTO createCategoryDTO) {
        return create(new Category(createCategoryDTO));
    }

    public Category create(Category category) {
        if (existsByName(category.getName(), false))
            throw new ResourceAlreadyExistsException("Category", "name(case-insensitive)", category.getName());
        return save(category);
    }

    public Category update(Long categoryId, UpdateCategoryDTO updateCategoryDTO) {
        Category existingCategory = getById(categoryId);
        if (updateCategoryDTO.getVisible() != null)
            existingCategory.setVisible(updateCategoryDTO.getVisible());
        if (updateCategoryDTO.getName() != null) {
            if (!updateCategoryDTO.getName().equals(existingCategory.getName()) && existsByName(updateCategoryDTO.getName(), false))
                throw new ResourceAlreadyExistsException("Category", "name(case-insensitive)", updateCategoryDTO.getName());
            existingCategory.setName(updateCategoryDTO.getName());
        }
        return save(existingCategory);
    }
}
