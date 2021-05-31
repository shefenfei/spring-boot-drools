package com.fisher.drools.springbootdrools.fact;

/**
 * @author shefenfei
 * @version 1.0
 * @desc
 * @date 2021年05月18日 3:46 PM
 */
public class Person {

    private Integer age;
    private String name;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
