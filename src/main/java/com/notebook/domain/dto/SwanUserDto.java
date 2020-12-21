package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: notebook
 * File: SwanUserDto
 *
 * @author evan
 * @date 2020/11/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwanUserDto {
    private String nickName;
    private String avatarUrl;
    private Integer gender;
}
