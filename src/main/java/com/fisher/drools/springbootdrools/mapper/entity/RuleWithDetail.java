package com.fisher.drools.springbootdrools.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月23日 10:41 AM
 */
@Data
public class RuleWithDetail {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(value = "rule_title")
    private String ruleName;
    @TableField(value = "rule_body")
    private String ruleBody;
    private int origin;
    private int target;
    private List<RuleDetail> details;
}
