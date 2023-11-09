package com.xoso.infrastructure.language;

import com.xoso.infrastructure.constant.ConstantCommon;
import com.xoso.infrastructure.util.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

public class MessageUtils {
    private static MessageSource messageSource;

    public static String message(String code, Object... args) {
        try {
            if (messageSource == null) {
                messageSource = BeanUtils.getBean(MessageSource.class);
            }
            var localeStr = MDC.get(ConstantCommon.MESSAGE_KEY);
            var locale = Locale.ROOT;
            if (StringUtils.isNotBlank(localeStr)) {
                locale = Locale.forLanguageTag(localeStr);
            }
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getLanguage() {
        return MDC.get(ConstantCommon.MESSAGE_KEY);
    }
}
