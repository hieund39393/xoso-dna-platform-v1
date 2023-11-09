
package com.xoso.infrastructure.service;

import com.xoso.client.model.Client;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class AccountNumberGenerator {
    private Random random = new Random(System.currentTimeMillis());
    private final static int maxLength = 9;

    public String generate(Client client) {
        var accountNumber = StringUtils.leftPad(client.getId().toString(), AccountNumberGenerator.maxLength, '0');
        return StringUtils.overlay(accountNumber, "", 0, 0);
    }

    public String generateDepositNumber() {
        return String.format("%s%s","DEP",generate("", 10));
    }

    public String generateTransaction() {
        return String.format("%s%s","T",generate("", 10));
    }

    public String generateWithdrawNumber() {
        return String.format("%s%s","WD",generate("", 10));
    }

    public String generate(String bin, int length) {

        int randomNumberLength = length - (bin.length() + 1);

        StringBuilder builder = new StringBuilder(bin);
        for (int i = 0; i < randomNumberLength; i++) {
            int digit = this.random.nextInt(10);
            builder.append(digit);
        }

        int checkDigit = this.getCheckDigit(builder.toString());
        builder.append(checkDigit);

        return builder.toString();
    }

    private int getCheckDigit(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {

            // Get the digit at the current position.
            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }
        int mod = sum % 10;
        return ((mod == 0) ? 0 : 10 - mod);
    }

}