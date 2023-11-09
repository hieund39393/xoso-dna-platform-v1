package com.xoso.campaign.wsdto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class TemplateContentRequestWsDTO {

    private Long id;
    @NotEmpty
    private String name;
    private String code;
    @NotEmpty
    private String category;

    private String html;

    private String banner;

    @NotEmpty
    private String language;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
    private boolean active;
}
