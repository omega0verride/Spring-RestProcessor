package org.indritbreti.restprocessordemo.API.appUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.indritbreti.restprocessor.IgnoreRESTField;
import org.indritbreti.restprocessordemo.API.appUser.DTO.CreateAppUserDTO;
import org.indritbreti.restprocessordemo.API.appUser.DTO.GetAppUserDTO;
import org.indritbreti.restprocessordemo.API.common.AuditData;
import org.indritbreti.restprocessordemo.API.common.Auditable;
import org.indritbreti.restprocessordemo.security.thirdPartyLogin.AuthType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AppUser implements Auditable {
    @Embedded
    AuditData auditData = new AuditData();


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private AuthType userAuthType = AuthType.BASIC;

    private String name;
    private String surname;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    @IgnoreRESTField
    private String password;

    private String phoneNumber;

    private String email;

    // internal
    @Column(name = "enabled")
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new HashSet<>();

    @Transient
    @IgnoreRESTField
    private boolean passwordUpdated = false;

    public AppUser(String name, String surname, String username, String password, String email, String phoneNumber, Collection<Role> roles) {
        setName(name);
        setSurname(surname);
        setUsername(username);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setPassword(password);
        setRoles(roles);
        setEnabled(false);
    }

    public AppUser(CreateAppUserDTO createAppUserDTO) {
        // no need to check for null values, these are all mandatory fields, the DTO will be marked invalid otherwise
        this(createAppUserDTO.getName(), createAppUserDTO.getSurname(), createAppUserDTO.getEmail(), createAppUserDTO.getPassword(), createAppUserDTO.getEmail(), createAppUserDTO.getPhoneNumber(), new ArrayList<>());
    }

    public static GetAppUserDTO toGetAppUserDTO(AppUser appUser) {
        return new GetAppUserDTO(appUser);
    }

    public void setPassword(String password) {
        if (!password.equals(this.password)) { // to avoid rehashing an already hashed password, causing auth issues
            this.password = password;
            passwordUpdated = true;
        }
    }

    @Transient
    @JsonIgnore
    public User toDomainUser() {
        return new User(this.getUsername(), "", this.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList()));
    }

    public GetAppUserDTO toGetAppUserDTO() {
        return new GetAppUserDTO(this);
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
