package org.indritbreti.restprocessordemo.API.appUser.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAppUserDTO {
    private String name;
    private String surname;
    private String phoneNumber;
    private Boolean isAdmin;
    private Boolean isEnabled;
}
