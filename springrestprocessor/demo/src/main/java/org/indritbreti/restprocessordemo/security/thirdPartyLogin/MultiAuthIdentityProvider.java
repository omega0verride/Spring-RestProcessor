package org.indritbreti.restprocessordemo.security.thirdPartyLogin;;





import org.indritbreti.restprocessordemo.security.DTO.MultiAuthIdentityProviderDTO;
import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;
import org.indritbreti.restprocessordemo.API.appUser.AppUser;
import org.indritbreti.restprocessordemo.API.appUser.AppUserService;
import org.indritbreti.restprocessordemo.exceptions.api.ResourceNotFoundException;
import org.indritbreti.restprocessordemo.exceptions.api.unauthorized.InvalidCredentialsException;
import org.indritbreti.restprocessordemo.exceptions.api.unauthorized.UserAccountNotActivatedException;
import org.indritbreti.restprocessordemo.exceptions.to_refactor.InvalidValueException;
import org.indritbreti.restprocessordemo.security.DTO.MultiAuthIdentityProviderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class MultiAuthIdentityProvider {

    // TODO[production] move to ENV VARIABLES
    @Getter
    private static final String ThirdPartyAccountPassword = "Eltl$@Q@K1iWQ4EoGOlm!TtLWu0y&Iux9ELr7ST9dU&vnsSYuK";

    @Autowired
    GoogleIdentity googleIdentity;
    @Autowired
    FacebookIdentity facebookIdentity;

    public UsernamePasswordAuthenticationToken getAuthenticationTokenFromAuthDetails(MultiAuthIdentityProviderDTO authDetails, AppUserService appUserService) {
        AppUser appUser = null;
        String password = getThirdPartyAccountPassword();
        if (authDetails.getAuthType() == AuthType.BASIC) {
            if (authDetails.getUsername() == null || authDetails.getUsername().trim().length() == 0) {
                throw new InvalidValueException("Username cannot be null or empty when AuthType=BASIC!");
            }
            if (authDetails.getPassword() == null || authDetails.getPassword().trim().length() == 0) {
                throw new InvalidValueException("Username cannot be null or empty when AuthType=BASIC!");
            }

            try {
                appUser = appUserService.getByUsername(authDetails.getUsername());
            } catch (ResourceNotFoundException resourceNotFoundException) {
                throw new InvalidCredentialsException(resourceNotFoundException);
            }
            if (!appUser.getUserAuthType().equals(AuthType.BASIC)) { // this is achieved only if someone tries to access a non BASIC account, the condition prevents accessing the third party provider accounts that have a common password
                throw new InvalidCredentialsException(new ResourceNotFoundException("User", "username", authDetails.getUsername()));
            }
            password = authDetails.getPassword();
        } else if (authDetails.getAuthType() == AuthType.GOOGLE) {
            appUser = appUserService.getOrRegisterUserInternally(googleIdentity.getAppUserFromToken(authDetails.getToken()));
        } else if (authDetails.getAuthType() == AuthType.INSTAGRAM) {
            throw new NotImplementedException("Instagram login is not implemented yet!");
//            appUser = appUserService.getOrRegisterUserInternally(facebookIdentity.getAppUserFromToken(authDetails.getToken()));
        } else if (authDetails.getAuthType() == AuthType.FACEBOOK) {
            appUser = appUserService.getOrRegisterUserInternally(facebookIdentity.getAppUserFromToken(authDetails.getToken()));
        }

        if (appUser == null) {
            throw new InvalidCredentialsException("Could not resolve user from provided authentication details.");
        }
        if (!appUser.isEnabled())
            throw new UserAccountNotActivatedException();
        return new UsernamePasswordAuthenticationToken(appUser.getUsername(), password);
    }
}
