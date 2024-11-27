package com.App.Yogesh.ResponseDto;

import lombok.Builder;

@Builder
public record MailBody(String to , String subject ,String text) {

}
