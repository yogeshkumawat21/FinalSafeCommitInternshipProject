package com.App.Yogesh.Repository;

import com.App.Yogesh.Models.ForgetPassword;
import com.App.Yogesh.Models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword, Integer> {

    @Query("select fp from ForgetPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgetPassword> findByOtpandUser(Integer otp, User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM ForgetPassword fp WHERE fp.user.id = ?1")
    void deleteByUserId(Integer userId);
}
