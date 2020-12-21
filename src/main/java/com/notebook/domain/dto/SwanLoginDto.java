package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: notebook
 * File: SwanLoginDto
 *
 * @author evan
 * @date 2020/11/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwanLoginDto {
    private SwanUserDto bdUserInfo;
    private String code;
}
