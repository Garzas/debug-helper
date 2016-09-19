package com.appunite.example.debugutilsexample.model;

public class Repos {

    private Integer id;
    private String name;
    private String description;
    private Boolean fork;
    private Owner owner;


    public Repos(Integer id, String name, String description, Boolean fork, Owner owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fork = fork;
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public Boolean getFork() {
        return fork;
    }

    public Owner getOwner() {
        return owner;
    }
}