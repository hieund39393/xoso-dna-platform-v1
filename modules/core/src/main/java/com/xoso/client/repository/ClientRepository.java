package com.xoso.client.repository;

import com.xoso.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("Select client from Client client where client.mobileNo = :mobileNo")
    Optional<Client> findClientByMobileNo(@Param("mobileNo") String mobileNo);
}
