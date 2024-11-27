package com.App.Yogesh.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BlackListedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BlacklistTokenId")
    private Integer blackListTokenId;

    @Column(name = "USerId")
    private Integer userId;

    //@Lob
    @Column(name = "BlacklistedToken")
    private String userToken;
}
