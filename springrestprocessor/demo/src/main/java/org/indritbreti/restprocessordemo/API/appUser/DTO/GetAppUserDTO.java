package org.indritbreti.restprocessordemo.API.appUser.DTO;

import org.indritbreti.restprocessordemo.API.appUser.AppUser;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.indritbreti.restprocessordemo.API.appUser.AppUser;
import org.indritbreti.restprocessordemo.API.common.AuditBaseDTO;
import org.indritbreti.restprocessordemo.security.AuthorizationFacade;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAppUserDTO extends AuditBaseDTO {
    private Long id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean enabled;

    private boolean isAdmin;

    public GetAppUserDTO(AppUser appUser) {
        this.id = appUser.getId();
        this.name = appUser.getName();
        this.surname = appUser.getSurname();
        this.username = appUser.getUsername();
        this.email = appUser.getEmail();
        this.phoneNumber = appUser.getPhoneNumber();
        this.enabled = appUser.isEnabled();
        this.createdAt = appUser.getCreatedAt();
        this.updatedAt = appUser.getUpdatedAt();
        this.isAdmin = appUser.getRoles().stream().anyMatch(r->r.getName().equals(AuthorizationFacade.ADMIN_AUTHORITY.getAuthority()));
    }

    public static GetAppUserDTO fromAppUser(AppUser appUser) {
        return new GetAppUserDTO(appUser);
    }
}
