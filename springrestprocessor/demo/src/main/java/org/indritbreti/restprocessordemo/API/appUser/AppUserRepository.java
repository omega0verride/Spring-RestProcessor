package org.indritbreti.restprocessordemo.API.appUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long>, org.indritbreti.restprocessordemo.API.appUser.AppUserDynamicQueryRepository {
    Optional<AppUser> findByUsername(String username);

    void deleteByUsername(String username);

    boolean existsByUsername(String username);

    AppUser getByUsername(String username);
}
