package com.xoso.user.service;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.user.data.UserData;
import com.xoso.user.model.AppUser;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface UserReadService {
    Collection<UserData> retrieveAllUsers();

    Page<UserData> search(SearchParameters searchParameter);

    UserData retrieveDetail(Long userId);

    UserData retrieveUser(Long userId);

    boolean isUsernameExist(String username);

    UserData findUserByUsername(String username);

    List<UserData> retrieveUserSystems();

    org.springframework.data.domain.Page<AppUser> retrieveAllAgencyUser(long agencyId, Pageable pageable, String searchValue);
}
