package com.xoso.user.service.impl;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.JdbcSupport;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.infrastructure.core.service.PaginationHelper;
import com.xoso.staff.data.StaffData;
import com.xoso.staff.service.StaffReadService;
import com.xoso.user.data.RoleData;
import com.xoso.user.data.UserData;
import com.xoso.user.exception.UserNotFoundException;
import com.xoso.user.model.AppUser;
import com.xoso.user.model.Role;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.user.repository.PasswordWithdrawRepository;
import com.xoso.user.service.RoleReadService;
import com.xoso.user.service.UserReadService;
import com.xoso.wallet.exception.WalletNotFountException;
import com.xoso.wallet.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class UserReadServiceImpl implements UserReadService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;
    private final RoleReadService roleReadService;
    private final AppUserRepository appUserRepository;
    private final StaffReadService staffReadService;
    private final PaginationHelper paginationHelper;
    private final PasswordWithdrawRepository passwordWithdrawRepository;
    private final WalletService walletService;

    @Autowired
    public UserReadServiceImpl(JdbcTemplate jdbcTemplate, RoleReadService roleReadService, AppUserRepository appUserRepository,
                               StaffReadService staffReadService, PaginationHelper paginationHelper,
                               PasswordWithdrawRepository passwordWithdrawRepository, WalletService walletService) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleReadService = roleReadService;
        this.appUserRepository = appUserRepository;
        this.staffReadService = staffReadService;
        this.paginationHelper = paginationHelper;
        this.walletService = walletService;
        this.passwordWithdrawRepository = passwordWithdrawRepository;
    }

    @Override
    public Collection<UserData> retrieveAllUsers() {
        final var mapper = new UserMapper(this.roleReadService, this.staffReadService);
        final String sql = "select " + mapper.schema();

        return this.jdbcTemplate.query(sql, mapper, new Object[] {});
    }

    @Override
    public Page<UserData> search(SearchParameters searchParameters) {
        final var mapper = new UserMapper(this.roleReadService, this.staffReadService);
        var paramList = new ArrayList<Object>();
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema());

        String extraCriteria = "";
        var searchValue = searchParameters.getSearchValue();

        if (StringUtils.isNotBlank(searchValue)) {
            extraCriteria += " and " +
                    "(u.username like ? " +
                    "or u.full_name like ? " +
                    "or u.mobile_no like ?)";
            var userInfo = "%" + searchValue + "%";
            paramList.add(userInfo);
            paramList.add(userInfo);
            paramList.add(userInfo);
        }

        sqlBuilder.append(extraCriteria);
        if (StringUtils.isNotBlank(searchParameters.getOrderBy())) {
            sqlBuilder.append(" order by ").append(searchParameters.getOrderBy());
        } else {
            sqlBuilder.append(" order by u.id DESC ");
        }

        final var sqlBuilderNoPaging = sqlBuilder.toString();
        if (searchParameters.isLimited()) {
            sqlBuilder.append(" limit ").append(searchParameters.getLimit());
            if (searchParameters.isOffset()) {
                sqlBuilder.append(" offset ").append(searchParameters.getLimit()*searchParameters.getOffset());
            }
        } else {
            sqlBuilder.append(" limit 100 offset 0 ");
        }
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlBuilder.toString(), sqlBuilderNoPaging,
                paramList.toArray(), mapper);
    }

    @Override
    public UserData retrieveDetail(Long userId) {
        final var mapper = new UserMapper(this.roleReadService, this.staffReadService);
        final String sql = "select " + mapper.schema() + " and u.id = ?";

        return this.jdbcTemplate.queryForObject(sql, mapper, userId);
    }

    @Override
    public UserData retrieveUser(Long userId) {
        final AppUser user = this.appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (user.isDeleted()) { throw new UserNotFoundException(userId); }

        final Collection<RoleData> availableRoles = this.roleReadService.retrieveAll();

        final Collection<RoleData> selectedUserRoles = new ArrayList<>();
        final Set<Role> userRoles = user.getRoles();
        for (final Role role : userRoles) {
            selectedUserRoles.add(role.toData());
        }

        availableRoles.removeAll(selectedUserRoles);

        final StaffData linkedStaff;
        if (user.getStaff() != null) {
            linkedStaff = this.staffReadService.retrieveStaff(user.getStaffId());
        } else {
            linkedStaff = null;
        }

        var retUser = UserData.instance(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getMobileNo(), availableRoles, selectedUserRoles, linkedStaff,
                user.isPasswordNeverExpires(), true, 0);

        return retUser;
    }

    @Override
    public boolean isUsernameExist(String username) {
        String sql = "select count(*) from m_appuser where username = ?";
        Object[] params = new Object[] { username };
        Integer count = this.jdbcTemplate.queryForObject(sql, params, Integer.class);
        logger.info("isUsernameExist {} - {}",username,count );
        if (count == 0) { return false; }
        return true;
    }

    @Override
    public UserData findUserByUsername(String username) {

        var appUser = this.appUserRepository.findAppUserByName(username);
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        var wallet = this.walletService.getWalletByUsername(username);
        if (wallet == null) {
            throw new WalletNotFountException();
        }

        var userData = UserData.builder()
                .id(appUser.getId())
                .username(appUser.getUsername())
                .mobileNo(appUser.getMobileNo())
                .email(appUser.getEmail())
                .fullName(appUser.getFullName())
                .balance(wallet.getBalance())
                .build();
        var passwordWithdraw = this.passwordWithdrawRepository.findByUser(appUser);
        if (!CollectionUtils.isEmpty(passwordWithdraw)) {
            userData.setHasPasswordWithdraw(Boolean.TRUE);
        } else {
            userData.setHasPasswordWithdraw(Boolean.FALSE);
        }
        return userData;
    }

    @Override
    public List<UserData> retrieveUserSystems() {
        final var mapper = new UserSystemOptionMapper();
        final String sql = "select " + mapper.schema();

        return this.jdbcTemplate.query(sql, mapper, new Object[] { });
    }

    private static final class UserMapper implements RowMapper<UserData> {

        private final RoleReadService roleReadService;
        private final StaffReadService staffReadService;

        private UserMapper(RoleReadService roleReadService, StaffReadService staffReadService) {
            this.roleReadService = roleReadService;
            this.staffReadService = staffReadService;
        }

        @Override
        public UserData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String username = rs.getString("username");
            final String fullName = rs.getString("fullName");
            final String email = rs.getString("email");
            final String mobileNo = rs.getString("mobileNo");
            final Long staffId = JdbcSupport.getLong(rs, "staffId");
            final Boolean passwordNeverExpire = rs.getBoolean("passwordNeverExpires");
            final Collection<RoleData> selectedRoles = this.roleReadService.retrieveAppUserRoles(id);
            final var accountNonLocked = rs.getBoolean("accountNonLocked");
            final var failedAttempt = rs.getInt("failedAttempt");

            StaffData linkedStaff = null;
            if (staffId != null) {
                linkedStaff = this.staffReadService.retrieveStaff(staffId);
            }
            return UserData.instance(id, username, email, fullName, mobileNo, null, selectedRoles, linkedStaff,
                    passwordNeverExpire, accountNonLocked, failedAttempt);
        }

        public String schema() {
            return " u.id as id, u.username as username, u.full_name as fullName, u.email as email,u.mobile_no as mobileNo," +
                    " u.password_never_expires as passwordNeverExpires, u.nonlocked as accountNonLocked, u.failed_attempt as failedAttempt,"
                    + " u.staff_id as staffId from app_user u "
                    + " where u.is_deleted=false ";
        }
    }

    private static final class UserLookupMapper implements RowMapper<UserData> {

        @Override
        public UserData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String username = rs.getString("username");

            return UserData.dropdown(id, username);
        }

        public String schema() {
            return " u.id as id, u.username as username from app_user u "
                    + " where u.is_deleted=false order by u.username";
        }
    }

    private static final class UserSystemOptionMapper implements RowMapper<UserData> {

        @Override
        public UserData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String username = rs.getString("username");

            return UserData.dropdown(id, username);
        }

        public String schema() {
            return " u.id as id, u.username as username from app_user u join user_role ur on ur.user_id = u.id join role r on r.id = ur.role_id "
                    + " where u.is_deleted=false and r.name = 'ADMIN' order by u.username";
        }
    }

    @Override
    public org.springframework.data.domain.Page<AppUser> retrieveAllAgencyUser(long agencyId, Pageable pageable, String searchValue) {
        return appUserRepository.findAllAgencyUser(agencyId,pageable);
    }
}
