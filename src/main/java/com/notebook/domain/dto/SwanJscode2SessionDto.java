package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: notebook
 * File: SwanJscode2SessionDto
 *
 * @author evan
 * @date 2020/11/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwanJscode2SessionDto {
    private Boolean success;
    private String openid;
    private String sessionKey;
    private Integer errno;
    private String error;
    private String errorDescription;
}
