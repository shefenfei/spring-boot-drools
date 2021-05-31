package com.fisher.drools.springbootdrools.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fisher.drools.springbootdrools.mapper.entity.Rule;
import com.fisher.drools.springbootdrools.mapper.entity.RuleWithDetail;

import java.util.List;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月22日 3:44 PM
 */
public interface RuleMapper extends BaseMapper<Rule> {

    List<RuleWithDetail> getAllRules();

}
