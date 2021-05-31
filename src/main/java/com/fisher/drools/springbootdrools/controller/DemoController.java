package com.fisher.drools.springbootdrools.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fisher.drools.springbootdrools.config.KieBaseHelper;
import com.fisher.drools.springbootdrools.fact.ClueFact;
import com.fisher.drools.springbootdrools.fact.Person;
import com.fisher.drools.springbootdrools.mapper.RuleDetailMapper;
import com.fisher.drools.springbootdrools.mapper.RuleMapper;
import com.fisher.drools.springbootdrools.mapper.entity.Rule;
import com.fisher.drools.springbootdrools.mapper.entity.RuleDetail;
import com.fisher.drools.springbootdrools.mapper.entity.RuleWithDetail;
import com.fisher.drools.springbootdrools.model.vo.RuleRequest;
import com.fisher.drools.springbootdrools.service.PersionDroolService;
import com.google.gson.Gson;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupString;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月18日 3:48 PM
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);
    @Resource
    private KieSession kieSession;
    @Autowired
    private PersionDroolService persionDroolService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/drools")
    public ResponseEntity filter() {
        Person person = new Person();
        person.setAge(40);
//        kieSession.insert(person);
//        kieSession.fireAllRules();
//        kieSession.dispose();
//        System.out.println(person.getName());

        KieSession kieSession = persionDroolService.newKieSession();
        kieSession.insert(person);
        kieSession.fireAllRules();
        kieSession.dispose();

        return ResponseEntity.ok(person.toString());
    }

    @Resource
    private RuleDetailMapper ruleDetailMapper;

    @PostMapping("/saveRule")
    public ResponseEntity saveRule(@RequestBody RuleRequest request) {
        Gson gson = new Gson();

//        redisTemplate.opsForValue().set("rule", condition.getAge());
        Rule rule = new Rule();
        rule.setOrigin(request.getOrigin());
        rule.setRuleName(UUID.randomUUID().toString());
        rule.setRuleBody(getClueTemplate());
        rule.setTarget(request.getTarget());
        rule.setCreateAt(new Date());
        ruleMapper.insert(rule);

        List<Map<String, Object>> rules = request.getRules();
        if (!CollectionUtils.isEmpty(rules)) {
            rules.stream().map(r -> {
                RuleDetail ruleDetail = new RuleDetail();
                ruleDetail.setRuleDetail(gson.toJson(r));
                ruleDetail.setRuleType(((Integer) r.get("type")));
                ruleDetail.setRuleId(rule.getId().longValue());
                ruleDetail.setTimeCondition(((Integer) r.get("day")));
                return ruleDetail;
            }).forEach(target -> {
                ruleDetailMapper.insert(target);
            });
        }

        return ResponseEntity.ok(request);
    }


    @Autowired
    private KieBaseHelper kieBaseHelper;

    @PostMapping("/verifyRule")
    public ResponseEntity verifyRule(@RequestBody ClueFact clueFact) {
        List<RuleWithDetail> allRules = ruleMapper.getAllRules();
        allRules.stream().map(ruleWithDetail -> {
            return buildRuleTemplate(ruleWithDetail);
        }).map(rule -> {
            return kieBaseHelper.ruleKieBase(rule);
        }).collect(Collectors.toList()).forEach(kieBase -> {
            KieSession kieSession = kieBase.newKieSession();
            kieSession.insert(clueFact);
            kieSession.fireAllRules();
            kieSession.dispose();
            LOGGER.info("{}", clueFact);
        });
        return ResponseEntity.ok(clueFact);
    }

    private String buildRuleTemplate(RuleWithDetail ruleWithDetail) {
        //{"day":15,"unSigned":true,"type":1}
        StringBuffer conditionStr = new StringBuffer();
        String ruleBody = ruleWithDetail.getRuleBody();
        List<RuleDetail> details = ruleWithDetail.getDetails();
        HashMap<String, Object> params = new HashMap<>();
        params.put("ruleName", ruleWithDetail.getRuleName());
        conditionStr.append("from==")
                .append(ruleWithDetail.getOrigin())
                .append("&& (");
        details.stream().forEach(detail -> {
            conditionStr.append(buildCondition(detail.getRuleDetail()));
        });

        if (conditionStr.toString().endsWith("||")) {
            int length = conditionStr.length();
            conditionStr.delete(length - 2, length);
        }

        conditionStr.append(")");
        params.put("condition", conditionStr.toString());
        params.put("result", ruleWithDetail.getTarget());

        String rule = buildTemplateRule(ruleBody, params);
        return rule;
    }

    private String buildCondition(String ruleDetail) {
        List<String> targetKeys = Arrays.asList("unSigned", "unFollowed", "closeCase");
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(ruleDetail, Map.class);
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        String str = "<if(unSigned)>hasDeal==<unSigned> ||<endif>" +
                     "<if(unFollowed)>hasFollowed==<unFollowed> ||<endif>" +
                     "<if(closeCase)>hasCloseCase==<closeCase><endif>";
        ST st = new ST(str);
        st.add("from", 1);
        entries.forEach(entity -> {
            st.add(entity.getKey(), entity.getValue());
        });
        return st.render();
    }


    public static void main(String[] args) {
        String str = "<if(from)>from==<from> &&<endif> (<if(unSigned)>hasDeal==<unSigned> ||<endif> " +
                "<if(unFollowed)>hasFollowed==<unFollowed> <endif> <if(unFollowed)> || <endif>" +
                "<if(closeCase)>hasCloseCase==<closeCase><endif>)";
        ST st = new ST(str);
        st.add("from", 1);
        st.add("unSigned", true);
        st.add("unFollowed", true);
        st.add("closeCase", true);
        String render = st.render();
        System.out.println(render);
    }


    public String buildTemplateRule(String rule, Map<String, Object> params) {
        String instanceName = "ruleTemplate";
        STGroupString stGroupString = new STGroupString(rule);
        ST instance = stGroupString.getInstanceOf(instanceName);
        instance.add("ruleName", params.get("ruleName"));
        instance.add("condition", params.get("condition"));
        instance.add("result", params.get("result"));
        String render = instance.render();
        return render;
    }


    private String getClueTemplate() {
        String template = "ruleTemplate(ruleName, condition, result) ::= <<" +
                "package rules;\n" +
                "import com.fisher.drools.springbootdrools.fact.ClueFact;\n" +
                "dialect 'mvel';\n" +
                "\n" +
                "rule <if(ruleName)>'<ruleName>'<endif>\n" +
                "   no-loop true\n" +
                "   when\n" +
                "       $c: ClueFact(<condition>);\n" +
                "   then\n" +
                "       $c.setTarget('<result>');\n" +
                "       update($c);\n" +
                "end>>";
        return template;
    }



    private String getTemplate() {
        return "ruleTemplate(ruleName, condition, result) ::= <<" +
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
    }


    @Resource
    private RuleMapper ruleMapper;

    @GetMapping("/list")
    public ResponseEntity getAllRule() {
        List<Rule> rules = ruleMapper.selectList(new QueryWrapper<>());
        return ResponseEntity.ok(rules);
    }

}
