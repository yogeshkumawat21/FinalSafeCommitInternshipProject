package com.App.Yogesh.Models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class ForgetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer forgetPasswordId;

    @Column(nullable = false)
    private Integer otp;
    @Column(nullable = false)
    private Date expirationTime;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private User user;

    public ForgetPassword(int otp, Date date, User user) {
        this.otp=otp;
        this.expirationTime=date;
        this.user=user;

    }
}
