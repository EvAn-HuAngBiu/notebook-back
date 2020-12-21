package com.notebook.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Project: notebook
 * File: SwanAddShareDto
 *
 * @author evan
 * @date 2020/11/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwanAddShareDto {
    private Integer tagId;

    private List<Integer> recordIds;
}
