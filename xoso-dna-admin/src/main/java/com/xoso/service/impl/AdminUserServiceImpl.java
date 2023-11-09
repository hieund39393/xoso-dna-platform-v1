package com.xoso.service.impl;

import com.xoso.infrastructure.security.service.PlatformPasswordEncoder;
import com.xoso.service.AdminUserService;
import com.xoso.user.exception.UserNotFoundException;
import com.xoso.user.repository.AppUserRepository;
import com.xoso.user.wsdto.GeneratePasswordWsDTO;
import lombok.AllArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AdminUserServiceImpl implements AdminUserService {

    private final AppUserRepository appUserRepository;
    private final PlatformPasswordEncoder platformPasswordEncoder;

    @Override
    public GeneratePasswordWsDTO generatePassword(Long userId) {
        var appUser = this.appUserRepository.queryFindAppUserById(userId);
        if (appUser == null) {
            throw new UserNotFoundException();
        }
        var passwordNew = generatePassword();
        appUser.setPassword(passwordNew);
        final String encodePassword = this.platformPasswordEncoder.encode(appUser);
        appUser.updatePassword(encodePassword);
        this.appUserRepository.save(appUser);
        var passwordWsDTO = new GeneratePasswordWsDTO();
        passwordWsDTO.setUsername(appUser.getUsername());
        passwordWsDTO.setPassword(passwordNew);
        passwordWsDTO.setMobileNo(appUser.getMobileNo());
        return passwordWsDTO;
    }

    private String generatePassword() {
        var gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        var lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(3);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(1);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(1);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        var password = gen.generatePassword(8, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }
}
