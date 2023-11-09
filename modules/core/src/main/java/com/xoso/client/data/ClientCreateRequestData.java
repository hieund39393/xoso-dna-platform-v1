package com.xoso.client.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientCreateRequestData {
    private String fullName;
    private String email;
    private String mobileNo;
    private String nationalId;
}
