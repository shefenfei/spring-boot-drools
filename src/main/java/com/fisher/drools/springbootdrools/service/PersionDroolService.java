package com.fisher.drools.springbootdrools.service;

import com.fisher.drools.springbootdrools.config.KieBaseHelper;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月18日 5:10 PM
 */
@Service
public class PersionDroolService {

    @Autowired
    private KieBaseHelper kieBaseHelper;

    public KieSession newKieSession() {
//        String rule = kieBaseHelper.activityRule(20);
//        String builderRule = kieBaseHelper.testBuilderRule(31);
        String rule  = kieBaseHelper.getRule();
        KieBase kieBase = kieBaseHelper.ruleKieBase(rule);
        return kieBase.newKieSession();
    }
}
