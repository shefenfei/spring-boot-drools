package com.fisher.drools.springbootdrools.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月22日 3:45 PM
 */
@Data
public class Rule extends Model<Rule> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(value = "rule_title")
    private String ruleName;
    @TableField(value = "rule_body")
    private String ruleBody;
    private int origin;
    private int target;
    private Date createAt;
    private Date updateAt;
    private boolean isDeleted;

}
