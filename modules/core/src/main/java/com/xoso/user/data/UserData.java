/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.xoso.user.data;

import lombok.Builder;
import lombok.Data;
import com.xoso.staff.data.StaffData;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class UserData {
    private Long id;
    private String username;
    private String staffName;
    private String fullName;
    private String email;
    private String mobileNo;
    private Boolean passwordNeverExpires;

    //import fields
    private List<Long> roles;
    private Collection<RoleData> availableRoles;
    private Collection<RoleData> selectedRoles;

    private Long staffId;
    private boolean accountNonLocked;
    private int failedAttempt;

    private Boolean hasPasswordWithdraw;
    private BigDecimal balance;

    public static UserData instance(final Long id, final String username, final String email, final String fullName, final String mobileNo,
                                    final Collection<RoleData> availableRoles, final Collection<RoleData> selectedRoles, final StaffData staff,
                                    final Boolean passwordNeverExpire, boolean accountNonLocked, int failedAttempt) {
        return new UserData(id, username, staff != null ? staff.getDisplayName() : null, fullName, email, mobileNo, passwordNeverExpire, null, availableRoles,
                selectedRoles, staff != null ? staff.getId() : null, accountNonLocked, failedAttempt, null, null);
    }

    public static UserData dropdown(Long id, String username) {
        return new UserData(id, username, null, null, null, null, null,
                null, null, null, null, true,0, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData that = (UserData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}