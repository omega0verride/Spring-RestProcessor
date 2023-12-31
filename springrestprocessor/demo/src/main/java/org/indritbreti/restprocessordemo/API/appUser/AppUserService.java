package org.indritbreti.restprocessordemo.API.appUser;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.indritbreti.restprocessor.DynamicRESTController.CriteriaParameters;
import org.indritbreti.restprocessordemo.API.appUser.DTO.UpdateAppUserDTO;
import org.indritbreti.restprocessordemo.API.common.BaseService;
import org.indritbreti.restprocessordemo.security.AuthorizationFacade;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

;

@Service
@Transactional
@Slf4j
public class AppUserService extends BaseService<AppUser> implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        super(appUserRepository, "User");
        this.appUserRepository = appUserRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsByUsername(String username) {
        return appUserRepository.existsByUsername(username);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = getByUsername(username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }


    // this method is used to register users internally, with no need to verify
    public AppUser getOrRegisterUserInternally(AppUser user) {
        AppUser appUser = getByUsername(user.getUsername());
        if (appUser != null)
            return appUser;
        user.setEnabled(true);
        user.getRoles().clear();
        if (user.isPasswordUpdated())
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        return save(user);
    }

    // used to register only
    public AppUser registerUser(AppUser user) {
        AppUser existingUserWithUsername = getByUsername(user.getUsername(), false);
        if (existingUserWithUsername != null)
            throw buildResourceAlreadyExistsException("username", user.getUsername());
        if (user.isPasswordUpdated())
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordUpdated(false);
        return appUserRepository.save(user);
    }

    // used to persist, do not use for register
    public AppUser saveUser(AppUser user) {
        AppUser existingUserWithUsername = getByUsername(user.getUsername(), false);
        if (existingUserWithUsername != null) {
            if (user.getId() != null && !user.getId().equals(existingUserWithUsername.getId())) {
                throw buildResourceAlreadyExistsException("username", user.getUsername());
            }
            user.setId(existingUserWithUsername.getId());
        }
        if (user.isPasswordUpdated())
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        return appUserRepository.save(user);
    }


    public void addRoleToUser(AppUser appUser, String roleName) {
        appUser.getRoles().add(roleService.getByName(roleName));
    }

    public void addRoleToUser(String username, String roleName) {
        addRoleToUser(getByUsername(username), roleName);
    }


    public void removeRoleFromUser(AppUser appUser, String roleName) {
        appUser.getRoles().remove(roleService.getByName(roleName));
    }

    public void removeRoleFromUser(String username, String roleName) {
        removeRoleFromUser(getByUsername(username), roleName);
    }

    @Override
    public void delete(Long id, boolean throwNotFoundEx) {
        if (existsById(id, throwNotFoundEx)) {
            AppUser appUser = getById(id);
            appUserRepository.deleteById(id);
        }
    }

    public void delete(String username) {
        AppUser appUser = getByUsername(username);
        if (appUser != null) {
            appUserRepository.delete(appUser);
        }
    }

    public void deleteAllUsersById(List<Long> ids) {
        appUserRepository.deleteAllById(ids);
    }

    public List<AppUser> getUsers() {
        log.info("Fetching all users");
        return appUserRepository.findAll();
    }

    public Page<AppUser> getAllByCriteria(CriteriaParameters cp) {
        return appUserRepository.findAllByCriteria(cp);
    }


    public AppUser getByUsername(String username) {
        return getByUsername(username, true);
    }

    public AppUser getByUsername(String username, boolean throwNotFoundEx) {
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        if (appUser.isPresent())
            return appUser.get();
        if (throwNotFoundEx)
            throw buildResourceNotFoundException("username", username);
        return null;
    }

    public void enableUser(AppUser appUser) {
        appUser.setEnabled(true);
        saveUser(appUser);
    }


    public boolean matchesPassword(AppUser user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }


    public AppUser updateUser(String username, UpdateAppUserDTO updateAppUserDTO) {
        AppUser existingAppUser = getByUsername(username);
        if (updateAppUserDTO.getName() != null)
            existingAppUser.setName(updateAppUserDTO.getName());
        if (updateAppUserDTO.getSurname() != null)
            existingAppUser.setSurname(updateAppUserDTO.getSurname());
        if (updateAppUserDTO.getPhoneNumber() != null)
            existingAppUser.setPhoneNumber(updateAppUserDTO.getPhoneNumber());
        if (updateAppUserDTO.getIsEnabled() != null) {
            AuthorizationFacade.ensureAdmin();
            existingAppUser.setEnabled(updateAppUserDTO.getIsEnabled());
        }
        if (updateAppUserDTO.getIsAdmin() != null) {
            AuthorizationFacade.ensureAdmin();
            if (updateAppUserDTO.getIsAdmin())
                addRoleToUser(existingAppUser, AuthorizationFacade.ADMIN_AUTHORITY.getAuthority());
            else
                removeRoleFromUser(existingAppUser, AuthorizationFacade.ADMIN_AUTHORITY.getAuthority());
        }
        return saveUser(existingAppUser);
    }
}
