package com.App.Yogesh.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String email;
    private String password;
    private List<Integer> followers;
    private List<Integer> followings;
}
