package com.xoso.api.security.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.xoso.user.data.RoleData;

import java.util.Collection;

@Data
@NoArgsConstructor
public class AuthenticatedUserData {
    private String username;
    private String fullName;
    private Long userId;
    private String base64EncodedAuthenticationKey;
    private String base64EncodedRefreshToken;
    private Long staffId;
    private String staffDisplayName;
    private Collection<RoleData> roles;
    private Collection<String> permissions;

    public AuthenticatedUserData(String username, Collection<String> permissions) {
        this.username = username;
        this.permissions = permissions;
    }

    public AuthenticatedUserData(String username, Long userId, String base64EncodedAuthenticationKey,
                                 Long staffId, String staffDisplayName, Collection<RoleData> roles, Collection<String> permissions) {
        this.username = username;
        this.userId = userId;
        this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
        this.staffId = staffId;
        this.staffDisplayName = staffDisplayName;
        this.roles = roles;
        this.permissions = permissions;
    }
}
