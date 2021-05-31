package com.fisher.drools.springbootdrools.config;

import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupString;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月18日 4:05 PM
 */
@Component
public class KieBaseHelper {

    @Autowired
    private RedisTemplate redisTemplate;

    //将业务规则写到规则库中
    public KieBase ruleKieBase(String rule) {//rule值就是动态传入的规则内容
        KieHelper helper = new KieHelper();
        KieBase KieBase = null;
        try {
            helper.addContent(rule, ResourceType.DRL);
            //为了省事，直接将rule写成activityRule()
            KieBase = helper.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return KieBase;
    }

    /**
     * 将来动态拼规则到数据库
     * @return
     */
    public String activityRule(Integer age) {
        //读取规则加入规则引擎中待执行
        Object a = redisTemplate.opsForValue().get("rule");
        String condition = "    $p:Person(age > "+ a +")\n";
        StringBuffer ruleDrl = new StringBuffer();
        ruleDrl.append("package rules \n ");
        ruleDrl.append("import   com.fisher.drools.springbootdrools.fact.Person; \n");
        ruleDrl.append("rule    \'person_1\' \n");
        ruleDrl.append("    no-loop true \n");
        ruleDrl.append("    salience  10 \n");
        ruleDrl.append("    when \n");
//        ruleDrl.append("    $p:Person(age > \'++\' )\n ");
        ruleDrl.append(     condition);
        ruleDrl.append("    then \n");
        ruleDrl.append("        $p.setName(\'老人1\');\n");
        ruleDrl.append("        update($p); \n");
        ruleDrl.append("end \n");
        return ruleDrl.toString();
    }


    public String testBuilderRule(Integer age) {
        String condition = "    $p:Person(age > "+ age +")\n";
        ArrayList<String> rules = new ArrayList<>();
        rules.add("package rules ");
        rules.add("import   com.fisher.drools.springbootdrools.fact.Person; ");
        rules.add("rule \'person_1\' ");
        rules.add("     no-loop true ");
        rules.add("     salience  10 ");
        rules.add("     when ");
        rules.add(          condition);
        rules.add("    then ");
        rules.add("         $p.setName(\'老人1\');");
        rules.add("         update($p); ");
        rules.add("end ");

        StringJoiner stringJoiner = new StringJoiner("\n");
        for (String rule : rules) {
            stringJoiner.add(rule);
        }

        String content = stringJoiner.toString();
        return content;
    }


    public String getRule() {
        String template = "ruleTemplate(ruleName, condition, result) ::= <<" +
                "package rules;\n" +
                "import com.fisher.drools.springbootdrools.fact.Person;\n" +
                "dialect 'mvel';\n" +
                "\n" +
                "rule <if(ruleName)>'<ruleName>'<endif>\n" +
                "   no-loop true\n" +
                "   when\n" +
                "       $p: Person(<condition>);\n" +
                "   then\n" +
                "       $p.setName('<result>');\n" +
                "       update($p);\n" +
                "end>>";

        String instanceName = "ruleTemplate";
        STGroupString stGroupString = new STGroupString(template);
        ST instance = stGroupString.getInstanceOf(instanceName);
        instance.add("ruleName", "rule_001");
        instance.add("condition", "age > 30");
        instance.add("result", "老人");

        String render = instance.render();
        return render;
    }


    public String buildTemplateRule(String rule) {
        String instanceName = "ruleTemplate";
        STGroupString stGroupString = new STGroupString(rule);
        ST instance = stGroupString.getInstanceOf(instanceName);
        instance.add("ruleName", "rule_001");
        instance.add("condition", "age > 30");
        instance.add("result", "老人");

        String render = instance.render();
        return render;
    }


}
