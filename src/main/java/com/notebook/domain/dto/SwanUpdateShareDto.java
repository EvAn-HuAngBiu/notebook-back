package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Project: notebook
 * File: SwanUpdateShareDto
 *
 * @author evan
 * @date 2020/11/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwanUpdateShareDto {
    private Integer shareId;

    private List<Integer> recordIds;
}
