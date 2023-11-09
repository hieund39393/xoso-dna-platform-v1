package com.xoso.language.repository;

import com.xoso.language.model.TranslatedString;
import com.xoso.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslatedStringRepository extends JpaRepository<TranslatedString, Long>, JpaSpecificationExecutor<TranslatedString> {
    List<TranslatedString> findByCode(String code);
}
