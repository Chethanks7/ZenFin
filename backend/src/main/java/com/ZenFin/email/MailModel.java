package com.ZenFin.email;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MailModel {

   @NotBlank
   private  String to;
   @Email(message = "not valid mail")
   @Valid
   private  String username;
   private  EmailTemplateName templateName;
   private  String activationUrl;
   private  String activationCode;
   private  String subject;

}
