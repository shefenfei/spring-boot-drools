package com.fisher.drools.springbootdrools.fact;

import lombok.Data;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月21日 1:11 PM
 */
@Data
public class ClueFact {

    /**
     * 来源(从规则配置中哪个池)
     */
    private Integer from;

    /**
     * 线索id
     */
    private Long clueId;

    /**
     * 是否已跟进 true 跟进; false 未跟进
     */
    private Boolean hasFollowed;

    /**
     * 是否结案 true: 结案；false : 未结案
     */
    private Boolean hasCloseCase;

    /**
     * 是否已转化 true：已转化；false ： 未转化
     */
    private Boolean hasDeal;

    /**
     * 移动到的目标池id
     */
    private Integer target;


}
