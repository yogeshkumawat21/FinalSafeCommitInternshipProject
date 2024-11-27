package com.App.Yogesh.Repository;

import com.App.Yogesh.Models.BlackListedToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, Integer> {
    Optional<BlackListedToken> findByUserToken(String userToken);
    @Transactional
    void deleteByUserToken(String userToken);

}
