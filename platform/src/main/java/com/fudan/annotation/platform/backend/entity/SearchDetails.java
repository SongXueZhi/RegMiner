package com.fudan.annotation.platform.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sxz
 * @Date: 2022/06/14/21:16
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDetails {
    private String searchSpaceNum;
    private List<String[]> orderList;
}
