package com.xoso.config;

import com.xoso.language.model.TranslatedString;
import com.xoso.language.repository.TranslatedStringRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.Locale;

@Component("messageSource")
public class CustomMessageSource extends AbstractMessageSource {

    @Autowired
    private TranslatedStringRepository repository;

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        var translatedStrings = repository.findByCode(code);
        var message = StringUtils.EMPTY;
        if (!CollectionUtils.isEmpty(translatedStrings)) {
            switch (locale.getLanguage()) {
                case "th":
                    message = translatedStrings.get(0).getThai();
                    break;
                case "km":
                    message = translatedStrings.get(0).getCam();
                    break;
                case "lo":
                    message = translatedStrings.get(0).getLao();
                    break;
                default:
                    message = translatedStrings.get(0).getViet();
                    break;
            }
        }
        return new MessageFormat(message, locale);
    }
}
