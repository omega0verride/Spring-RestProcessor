package org.indritbreti.restprocessordemo.security.DTO;

import org.indritbreti.restprocessordemo.security.thirdPartyLogin.AuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.indritbreti.restprocessordemo.security.thirdPartyLogin.AuthType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultiAuthIdentityProviderDTO extends BasicCredentialsDTO {
    String token;
    AuthType authType = AuthType.BASIC;
}
