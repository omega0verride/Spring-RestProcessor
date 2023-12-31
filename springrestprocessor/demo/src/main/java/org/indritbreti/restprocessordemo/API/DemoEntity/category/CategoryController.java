package org.indritbreti.restprocessordemo.API.DemoEntity.category;;



import org.indritbreti.restprocessordemo.API.common.responseFactory.ResponseFactory;



import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.CreateCategoryDTO;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.GetCategoryDTO;
import org.indritbreti.restprocessordemo.API.DemoEntity.category.DTO.UpdateCategoryDTO;
import org.indritbreti.restprocessordemo.API.appUser.AppUserService;
import org.indritbreti.restprocessordemo.API.common.responseFactory.ResponseFactory;
import org.indritbreti.restprocessordemo.exceptions.api.forbidden.ResourceRequiresAdminPrivileges;
import org.indritbreti.restprocessordemo.security.AuthorizationFacade;
import org.indritbreti.restprocessordemo.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final JwtUtils jwtUtils;

    private final AppUserService appUserService;

    @Autowired
    public CategoryController(CategoryService categoryService, JwtUtils jwtUtils, AppUserService appUserService) {
        this.categoryService = categoryService;
        this.jwtUtils = jwtUtils;
        this.appUserService = appUserService;
    }

    @GetMapping("")
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public List<GetCategoryDTO> getAllCategories(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        List<Category> categories;
        if (!AuthorizationFacade.isAdminAuthorization(authorizationHeader, jwtUtils, appUserService))
            categories = categoryService.getCategoriesIfVisible();
        else
            categories = categoryService.getCategories();
        return categories.stream().map(c -> c.toGetCategoryDTO()).collect(Collectors.toList());
    }

    @GetMapping("/{categoryId}")
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public GetCategoryDTO getCategoryById(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader, @PathVariable(name = "categoryId", required = true) Long categoryId) {
        Category category = categoryService.getById(categoryId);
        if (!category.isVisible() && !AuthorizationFacade.isAdminAuthorization(authorizationHeader, jwtUtils, appUserService))
            throw new ResourceRequiresAdminPrivileges("Category", "Id", categoryId.toString());
        return category.toGetCategoryDTO();
    }

    @PostMapping("")
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CreateCategoryDTO createCategoryDTO) {
        AuthorizationFacade.ensureAdmin();
        Category insertedCategory = categoryService.create(createCategoryDTO);
        return ResponseFactory.buildResourceCreatedSuccessfullyResponse("Category", "categoryId", insertedCategory.getId());
    }

    @PatchMapping("/{categoryId}")
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Object> updateCategory(@PathVariable(name = "categoryId", required = true) Long categoryId, @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO) {
        AuthorizationFacade.ensureAdmin();
        Category updatedCategory = categoryService.update(categoryId, updateCategoryDTO);
        return ResponseFactory.buildResourceUpdatedSuccessfullyResponse("Category", "categoryId", categoryId, updatedCategory.toGetCategoryDTO());
    }

    @DeleteMapping("/{categoryId}")
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Object> deleteCategory(@PathVariable(name = "categoryId", required = true) Long categoryId) {
        AuthorizationFacade.ensureAdmin();
        categoryService.delete(categoryId);
        return ResponseFactory.buildResourceDeletedSuccessfullyResponse();
    }
    // TODO [0]: if we have time we can add an endpoint for batch deletes

}
