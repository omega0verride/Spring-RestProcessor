package org.indritbreti.restprocessordemo.API.appUser;

import org.indritbreti.restprocessordemo.API.common.BaseService;

import jakarta.transaction.Transactional;
import org.indritbreti.restprocessordemo.API.common.BaseService;
import org.indritbreti.restprocessordemo.exceptions.api.ResourceAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoleService extends BaseService<Role> {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        super(roleRepository, "Role");
        this.roleRepository = roleRepository;
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public Role getByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> {
            throw buildResourceNotFoundException("name", name);
        });
    }

    @Override
    public Role save(Role role) {
        if (existsByName(role.getName()))
            throw new ResourceAlreadyExistsException("Role", "name", role.getName());
        return super.save(role);
    }
}
