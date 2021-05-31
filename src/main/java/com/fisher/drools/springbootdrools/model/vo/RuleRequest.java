package com.fisher.drools.springbootdrools.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月22日 4:13 PM
 */
@Data
public class RuleRequest {

    private int origin;
    private int target;
    private List<Map<String, Object>> rules;
    private String operator;
}
