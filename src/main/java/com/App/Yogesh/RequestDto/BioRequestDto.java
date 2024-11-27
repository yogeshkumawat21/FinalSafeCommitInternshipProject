package com.App.Yogesh.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
 public class BioRequestDto {

    private int userId;
    private String bioMessage;
 }
