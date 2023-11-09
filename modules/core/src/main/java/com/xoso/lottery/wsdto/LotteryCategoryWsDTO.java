package com.xoso.lottery.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotteryCategoryWsDTO {
    private Long id;
    private String name;
    private String code;
    private boolean active;
    private boolean campaign;
}
