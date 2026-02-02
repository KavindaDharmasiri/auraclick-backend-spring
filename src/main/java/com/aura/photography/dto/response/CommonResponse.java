package com.aura.photography.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: kasun
 * @Package: com.aura.photography.dto.response
 * @Class: CommonResponse
 * @Created on: 1/31/2026 at 3:29 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {

    private String statusCode;
    private String message;
    private Object data;
    private Integer count;
}
