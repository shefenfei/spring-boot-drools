package com.fisher.drools.springbootdrools.utils;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import java.util.HashMap;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月20日 5:49 PM
 */
public class DemoStringTemplate {


    public static void main(String[] args) {
//        testBasicTemplate();
//        testTemplate();
//        testAdvanceTemplate();
        testTemplateGroup();
    }

    private static void testBasicTemplate() {
        String rule = "package <packageName>\n " +
                "rule <ruleName>\n";

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("packageName", "com.fisher.demo");
        stringObjectHashMap.put("ruleName", "rule_001");
        ST st = new ST(rule);
        stringObjectHashMap.forEach(st::add);
        String renderString = st.render();
        System.out.println(renderString);
    }


    public static String message(String format) {
        ST st = new ST(format);
        return "";
    }

    public static void testTemplate() {
        String template = "hello, $if(name)$$name$$endif$";
        //第二种方式非常适合拼字符串
        String template1 = "hello , <if(name)><name><endif>";
        ST st = new ST(template1);
        st.add("name", "shefenfei1");
        String render = st.render();
        System.out.println(render);
    }


    public static void testAdvanceTemplate() {
        String template = "templateName(args1, args2,...) :: += \\模板内容";

        STGroup stg = new STGroupString("sqlTemplate(columns,condition) ::= \"select <columns> from table where 1=1 <if(condition)>and <condition><endif> \"");
        ST sqlST = stg.getInstanceOf("sqlTemplate");
        sqlST.add("columns","order_id");
        sqlST.add("condition", "dt='2017-04-04'");
        System.out.print(sqlST.render());
    }

    public static void testTemplateGroup() {
        String str = "package rules;\n" +
                "import com.fisher.drools.springbootdrools.fact.Person;\n" +
                "dialect  \"mvel\"\n" +
                "\n" +
                "rule \"hello\"\n" +
                "    no-loop true\n" +
                "    when\n" +
                "        $p: Person(age > 30)\n" +
                "    then\n" +
                "        $p.setName(\"老人\");\n" +
                "        update($p);\n" +
                "end";
        String template = "ruleTemplate(ruleName, condition, result) ::= <<" +
                "package rules;\n" +
                "dialect 'mvel';\n" +
                "\n" +
                "rule <if(ruleName)>'<ruleName>'<endif>\n" +
                "   no-loop true\n" +
                "   when\n" +
                "       $p: Person(<condition>);\n" +
                "   then\n" +
                "       $p.setName(<result>);\n" +
                "       update($p);\n" +
                "end>>";

        String instanceName = "ruleTemplate";
        STGroupString stGroupString = new STGroupString(template);
        ST instance = stGroupString.getInstanceOf(instanceName);
        instance.add("ruleName", "rule_001");
        instance.add("condition", "age > 30");
        instance.add("result", "老人");

        String render = instance.render();
        System.out.println(render);
    }
}
