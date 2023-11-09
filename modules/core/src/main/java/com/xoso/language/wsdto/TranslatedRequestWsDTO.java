package com.xoso.language.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslatedRequestWsDTO {
    private Long id;
    private String code;
    private String viet;
    private String thai;
    private String lao;
    private String cam;
}
