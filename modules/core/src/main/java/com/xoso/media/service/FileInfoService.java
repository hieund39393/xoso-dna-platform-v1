package com.xoso.media.service;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.infrastructure.core.service.Page;
import com.xoso.media.wsdto.FileInfoWsDTO;

public interface FileInfoService {
    Page<FileInfoWsDTO> retrieveAll(SearchParameters searchParameters);
    FileInfoWsDTO retrieveOne(Long id);
}
