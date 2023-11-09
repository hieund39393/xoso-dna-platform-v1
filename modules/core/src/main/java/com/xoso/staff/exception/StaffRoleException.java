
package com.xoso.staff.exception;

import com.xoso.infrastructure.core.exception.AbstractPlatformException;

public class StaffRoleException extends AbstractPlatformException {

    public static enum STAFF_ROLE {
        LOAN_OFFICER, BRANCH_MANAGER,SAVINGS_OFFICER;

        @Override
        public String toString() {
            return name().toString().replaceAll("-", " ").toLowerCase();
        }
    }

    public StaffRoleException(final Long id, final STAFF_ROLE role) {
        super("error.msg.staff.id.invalid.role", "Staff with identifier " + id + " is not a " + role.toString(), id);
    }
}