package com.App.Yogesh.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {
        private String firstName;
        private String middleName;
        private String lastName;
        private String gender;
    }


