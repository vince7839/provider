package com.sagereal.provider.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity(name = "issue")
public class Issue {
    @Id
    @GeneratedValue
    Integer issueId;

    String eServiceId;
    Integer priority;
    String reason;

    @Column(length = 1024)
    String description;

    Integer projectType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date expectDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date submitDate;

    String proposer;
    String impact;
    String state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date modifyDate;

    String mail;
    String support;
}
