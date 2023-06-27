package org.indritbreti.restprocessordemo.API.DemoEntity;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.Filter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.FilterFactory;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.NumericFilter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.RHSColonExpression;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.PathFunctionArg;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.SortByFunction;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.SortOrder;
import org.indritbreti.restprocessor.DynamicRESTController.CriteriaParameters;
import org.indritbreti.restprocessor.DynamicRestMapping;
import org.indritbreti.restprocessor.FieldDetailsRegistry;
import org.indritbreti.restprocessor.RequestMethod;
import org.indritbreti.restprocessordemo.API.DemoEntity.DTO.GetMinimalProductDTO;
import org.indritbreti.restprocessordemo.API.DemoEntity.DTO.GetModerateProductDTO;
import org.indritbreti.restprocessordemo.API.DemoEntity.DTO.GetProductDTO;
import org.indritbreti.restprocessordemo.API.appUser.AppUserService;
import org.indritbreti.restprocessordemo.API.common.responseFactory.PageResponse;
import org.indritbreti.restprocessordemo.API.common.responseFactory.ResponseFactory;
import org.indritbreti.restprocessordemo.exceptions.api.forbidden.ResourceRequiresAdminPrivileges;
import org.indritbreti.restprocessordemo.security.AuthorizationFacade;
import org.indritbreti.restprocessordemo.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

;

@RestController
@RequestMapping(path = "api/products")
public class ProductController {
    private final org.indritbreti.restprocessordemo.API.DemoEntity.ProductService productService;

    private final AppUserService appUserService;
    private final org.indritbreti.restprocessordemo.API.DemoEntity.ProductRepository productRepository;

    private final JwtUtils jwtUtils;

    @Autowired
    public ProductController(org.indritbreti.restprocessordemo.API.DemoEntity.ProductService productService, AppUserService appUserService, org.indritbreti.restprocessordemo.API.DemoEntity.ProductRepository productRepository, JwtUtils jwtUtils) {
        this.productService = productService;
        this.appUserService = appUserService;
        this.productRepository = productRepository;
        this.jwtUtils = jwtUtils;
        FieldDetailsRegistry.instance().bindField(org.indritbreti.restprocessordemo.API.DemoEntity.Product.class, new SortByFunction<Float>("custom_ts_rank", Float.class, "searchBestMatch", 1, SortOrder.DESC));
        FieldDetailsRegistry.instance().bindField(org.indritbreti.restprocessordemo.API.DemoEntity.Product.class, new SortByFunction<Float>("length", Long.class, "descriptionLength", 1, SortOrder.ASC, new PathFunctionArg(0, "description")));
    }

    @GetMapping("/{productId}")
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public GetProductDTO getProductByID(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader, @PathVariable(name = "productId", required = true) Long productId) {
        org.indritbreti.restprocessordemo.API.DemoEntity.Product product = productService.getById(productId);
        if (!product.isVisible() && !AuthorizationFacade.isAdminAuthorization(authorizationHeader, jwtUtils, appUserService))
            throw new ResourceRequiresAdminPrivileges("Product", "Id", productId.toString());
        return new GetProductDTO(product);
    }

    @DynamicRestMapping(path = "", requestMethod = org.indritbreti.restprocessor.RequestMethod.GET, entity = org.indritbreti.restprocessordemo.API.DemoEntity.Product.class)
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<GetModerateProductDTO>> getAllProducts(CriteriaParameters cp, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader, @RequestParam(name = "searchQuery", required = false) String searchQuery) {
        Page<Product> resultsPage = productService.getAllByCriteria(!AuthorizationFacade.isAdminAuthorization(authorizationHeader, jwtUtils, appUserService), searchQuery, cp);
        return ResponseFactory.buildPageResponse(resultsPage, GetModerateProductDTO::new);
    }

    @DynamicRestMapping(path = "/searchSuggestions", requestMethod = RequestMethod.GET, entity = org.indritbreti.restprocessordemo.API.DemoEntity.Product.class)
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<GetMinimalProductDTO>> getProductsSearchSuggestion(CriteriaParameters cp, @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader, @RequestParam(name = "searchQuery", required = false) String searchQuery) {
        Page<org.indritbreti.restprocessordemo.API.DemoEntity.Product> resultsPage = productService.getAllByCriteria(!AuthorizationFacade.isAdminAuthorization(authorizationHeader, jwtUtils, appUserService), searchQuery, cp);
        return ResponseFactory.buildPageResponse(resultsPage, product -> new GetMinimalProductDTO(product, productService));
    }


    @GetMapping("demoFilters")
    public ResponseEntity<PageResponse<GetModerateProductDTO>> demoFilters(@RequestParam(name = "id", required = false) List<RHSColonExpression> idFilters) {
        List<Filter<?>> filters = new ArrayList<>();
        filters.addAll(FilterFactory.getNumericFiltersFromRHSColonExpression(Long.class, "id", idFilters));
        filters.add(new NumericFilter<Long>("id", CriteriaOperator.NOT_EQUAL, 2L));
        Page<org.indritbreti.restprocessordemo.API.DemoEntity.Product> resultsPage = productRepository.findAllByCriteria(0, 30, null, filters, null);
        return ResponseFactory.buildPageResponse(resultsPage, GetModerateProductDTO::new);
    }
}