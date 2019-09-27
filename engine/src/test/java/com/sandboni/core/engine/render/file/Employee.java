package com.sandboni.core.engine.render.file;

import java.util.List;

import javax.xml.bind.annotation.*;


@XmlRootElement
public class Employee {

    @XmlElement
    private String name;
    @XmlElement
    private int age;
    @XmlElement
    private double pay;
    @XmlElement
    private List<String> projects;

    public Employee(){}

    public Employee(String name, int age, double pay, List<String> projects) {
        this.name = name;
        this.age = age;
        this.pay = pay;
        this.projects = projects;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getPay() {
        return pay;
    }

    public List<String> getProjects() {
        return projects;
    }
}


