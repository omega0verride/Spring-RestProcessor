package org.indritbreti.restprocessordemo.API.appUser;;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;

import org.indritbreti.restprocessor.DynamicRESTController.CriteriaParameters;
import org.indritbreti.restprocessor.DynamicRestMapping;
import org.indritbreti.restprocessor.RequestMethod;
import org.indritbreti.restprocessordemo.API.appUser.DTO.GetAppUserDTO;
import org.indritbreti.restprocessordemo.API.common.responseFactory.PageResponse;
import org.indritbreti.restprocessordemo.API.common.responseFactory.ResponseFactory;
import org.indritbreti.restprocessordemo.security.AuthorizationFacade;
import org.indritbreti.restprocessordemo.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;
    private final JwtUtils jwtUtils;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @DynamicRestMapping(path = "", entity = AppUser.class, requestMethod = RequestMethod.GET)
    @SecurityRequirements(@SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<GetAppUserDTO>> getUsers(CriteriaParameters cp) {
        AuthorizationFacade.ensureAdmin();
        return ResponseFactory.buildPageResponse(appUserService.getAllByCriteria(cp), GetAppUserDTO::new);
    }
}

