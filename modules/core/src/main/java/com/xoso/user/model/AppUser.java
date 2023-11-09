package com.xoso.user.model;

import com.xoso.client.model.Client;
import lombok.Getter;
import lombok.Setter;
import com.xoso.infrastructure.core.model.AbstractAuditableCustom;
import com.xoso.infrastructure.security.domain.PlatformUser;
import com.xoso.staff.model.Staff;
import com.xoso.user.wsdto.UserCreateRequestWsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(columnNames = { "username" }, name = "username_org"))
public class AppUser extends AbstractAuditableCustom implements PlatformUser {

    private final static Logger logger = LoggerFactory.getLogger(AppUser.class);

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "mobile_no", length = 50, unique = true, nullable = false)
    private String mobileNo;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nonexpired", nullable = false)
    private boolean accountNonExpired;

    @Column(name = "nonlocked", nullable = false)
    private boolean accountNonLocked;

    @Column(name = "nonexpired_credentials", nullable = false)
    private boolean credentialsNonExpired;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "last_time_password_updated")
    private LocalDate lastTimePasswordUpdated;

    @Column(name = "password_never_expires", nullable = false)
    private boolean passwordNeverExpires;

    @Column(name = "secret_key", nullable = true)
    private String secretKey;

    @Column(name = "failed_attempt")
    private int failedAttempt;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "agency_id")
    private Long agencyId;

    @Column(name = "agency_level")
    private int agencyLevel;

    @Column(name = "agency_status")
    private int agencyStatus;

    public AppUser(final User user, final Set<Role> roles, final String email, final String fullName,
                   final String mobileNo, final Staff staff, final boolean passwordNeverExpire) {
        this.email = Objects.nonNull(email) ? email.trim() : null;
        this.username = user.getUsername().trim();
        this.fullName = Objects.nonNull(fullName) ? fullName.trim() : null;
        this.password = user.getPassword().trim();
        this.accountNonExpired = user.isAccountNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.enabled = user.isEnabled();
        this.roles = roles;
        this.lastTimePasswordUpdated = LocalDate.now();
        this.staff = staff;
        this.passwordNeverExpires = passwordNeverExpire;
        this.mobileNo = mobileNo;
    }

    public AppUser() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return populateGrantedAuthorities();
    }

    public static AppUser fromRequest(final Staff linkedStaff, final Set<Role> allRoles, final UserCreateRequestWsDTO request) {

        final var username = request.getUsername();
        final var password = request.getPassword();
        final var passwordNeverExpire = true;

        final boolean userEnabled = true;
        final boolean userAccountNonExpired = true;
        final boolean userCredentialsNonExpired = true;
        final boolean userAccountNonLocked = true;

        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("DUMMY_ROLE_NOT_USED_OR_PERSISTED_TO_AVOID_EXCEPTION"));

        final var user = new User(username, password, userEnabled, userAccountNonExpired, userCredentialsNonExpired, userAccountNonLocked,
                authorities);

//        final String email = request.getEmail();
//        final String fullName = request.getFullName();

        return new AppUser(user, allRoles, "", "", request.getMobileNo(), linkedStaff, passwordNeverExpire);
    }

    private List<GrantedAuthority> populateGrantedAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (final Role role : this.roles) {
            final Collection<Permission> permissions = role.getPermissions();
            for (final Permission permission : permissions) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getCode()));
            }
        }
        return grantedAuthorities;
    }

    public Long getStaffId() {
        Long staffId = null;
        if (this.staff != null) {
            staffId = this.staff.getId();
        }
        return staffId;
    }

    public String getStaffDisplayName() {
        String staffDisplayName = null;
        if (this.staff != null) {
            staffDisplayName = this.staff.getDisplayName();
        }
        return staffDisplayName;
    }

    public void updatePassword(final String encodePassword) {
        this.password = encodePassword;
        this.lastTimePasswordUpdated = LocalDate.now();
    }

    public void delete() {
        this.deleted = true;
        this.enabled = false;
        this.accountNonExpired = false;
        this.username = getId() + "_DELETED_" + this.username;
        this.roles.clear();
    }
}