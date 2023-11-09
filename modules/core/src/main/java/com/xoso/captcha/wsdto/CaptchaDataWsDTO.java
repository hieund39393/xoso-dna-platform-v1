package com.xoso.captcha.wsdto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
@Data
public class CaptchaDataWsDTO {
    public String image;
    public String id;
}
