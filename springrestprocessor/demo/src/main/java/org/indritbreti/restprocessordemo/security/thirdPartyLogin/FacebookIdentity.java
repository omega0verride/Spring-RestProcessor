package org.indritbreti.restprocessordemo.security.thirdPartyLogin;

import com.fasterxml.jackson.databind.JsonNode;
import org.indritbreti.restprocessordemo.API.appUser.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.indritbreti.restprocessordemo.API.appUser.AppUser;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FacebookIdentity extends ThirdPartyIdentityBase {
    public FacebookIdentity() {
        super("https://graph.facebook.com/me?&fields=id,first_name,last_name,email&access_token=", "id", "FACEBOOK_ID_", "invalid_token");
    }

    @Override
    protected void mapExtraUserDetails(AppUser appUser, JsonNode root) {
        appUser.setEmail(tryGetValue(root, "email", false));
        appUser.setName(tryGetValue(root, "first_name", false));
        appUser.setSurname(tryGetValue(root, "last_name", false));
    }
}
