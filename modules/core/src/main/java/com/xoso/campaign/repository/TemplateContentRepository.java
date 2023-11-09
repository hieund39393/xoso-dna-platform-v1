package com.xoso.campaign.repository;

import com.xoso.campaign.model.TemplateContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateContentRepository extends JpaRepository<TemplateContent, Long>, JpaSpecificationExecutor<TemplateContent> {

    @Query("select w from TemplateContent w where w.id = ?1")
    TemplateContent queryFindById(Long id);
}
