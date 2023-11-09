package com.xoso.language.data;

import com.xoso.language.model.TranslatedString;
import lombok.*;

import java.util.List;
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class TranslatedStringData {
    Long id;
    String code;
    String viet;
    String thai;
    String lao;
    String cam;
    public static TranslatedStringData fromEntity(TranslatedString entity){
        return TranslatedStringData.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .viet(entity.getViet())
                .thai(entity.getThai())
                .lao(entity.getLao())
                .cam(entity.getCam())
                .build();
    }
}
