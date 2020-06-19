package com.rmr101.campus.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(length = 16)
    private UUID uuid; //uuid

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    private List<StudentCourse> courses = new ArrayList<StudentCourse>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    private List<StudentAssignment>  assignments = new ArrayList<StudentAssignment>();
}
