package com.xoso.user.wsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestWsDTO {
    private Long id;
    private String username;
    private String fullName;
    @NotNull
    private String mobileNo;
    private String email;
}
