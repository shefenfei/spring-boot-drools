package com.fisher.drools.springbootdrools.mapper.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月22日 4:20 PM
 */
@Data
public class RuleDetail extends Model<RuleDetail> {

    private long id;
    private Long ruleId;
    private String ruleDetail;
    private Integer ruleType;
    private Integer timeCondition;

}
