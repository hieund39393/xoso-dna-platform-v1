package com.xoso.campaign.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateContentWsDTO {
    private Long id;
    private String name;
    private String code;
    private String banner;
    private String category;
    private String html;
    private String language;
    private String startDate;
    private String endDate;
    private boolean active;
    private String bannerUrl;
}
