package com.ZenFin.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private int businessErrorCode;
    private String businessErrorDescription;
    private String error;
    private Set<String> validateErrors;
    private Map<String, String> errors;

}

