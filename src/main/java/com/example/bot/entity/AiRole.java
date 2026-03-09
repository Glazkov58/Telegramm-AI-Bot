package com.example.bot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_roles")
public class AiRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // или AUTO, SEQUENCE и т.д., в зависимости от вашей БД
    private Long id;

    private String title;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "parent_role_id")
    //private AiRole parentRole;

    @ManyToOne
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private AiModel model;

    private String prompt;
    @JoinColumn(name = "is_active")
    private boolean isActive = true;

    public AiRole(String role, String prompt) {
        title = role;
        this.prompt = prompt;
    }

    public AiRole() {

    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    /*
    public AiRole getParentRole() {
        return parentRole;
    }
    public void setParentRole(AiRole parentRole) {
        this.parentRole = parentRole;
    }
    */
    public AiModel getModel() {
        return model;
    }
    public void setModel(AiModel model) {
        this.model = model;
    }
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    
}
