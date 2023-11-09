package com.xoso.media.wsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoWsDTO {
    private Long id;
    private String fileName;
    private String url;
}
