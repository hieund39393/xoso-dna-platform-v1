package com.xoso.bank.data;

import com.xoso.bank.model.Bank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankData {
    private long id;
    private String code;
    private String name;
    private String description;
    private boolean enabled;

    public static BankData fromEntity(Bank entity){
        return BankData.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .enabled(entity.isEnabled())
                .build();
    }
}
