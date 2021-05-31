package com.fisher.drools.springbootdrools.utils;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月20日 5:44 PM
 */

public class BaseTemplate {
    public static final String workMoneyST = "wordImport(rules) ::=<<\n" +
            "package com.promote\n" +
            "\n" +
            "import\tcom.droolsBoot.model.RuleResult;\n" +
            "<rules; separator=\"\\n\\n\">\n" +
            ">>\n" +
            "\n" +
            "ruleValue(condition,action,rule) ::=<<\n" +
            "rule \"<rule.name>\"\n" +
            "\tno-loop true\n" +
            "\t\twhen\n" +
            "\t\t    $r:RuleResult(true)\n" +
            " \t\tthen\n" +
            "           modify($r){\n" +
            "                setPromoteName(drools.getRule().getName())<if(action)>,\n" +
            "                setFinallyMoney($r.getMoneySum() - <action.money><endif>)\n" +
            "           }\n" +
            "end\n" +
            ">>\n";
}
