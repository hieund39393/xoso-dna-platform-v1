package com.xoso.infrastructure.security.service.impl;

import com.xoso.infrastructure.security.domain.PlatformUser;
import com.xoso.infrastructure.security.service.PlatformPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service(value = "applicationPasswordEncoder")
@Scope("singleton")
public class PlatformPasswordEncoderImpl implements PlatformPasswordEncoder {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String encode(PlatformUser user) {
        return this.bCryptPasswordEncoder.encode(user.getPassword());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return this.bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}
