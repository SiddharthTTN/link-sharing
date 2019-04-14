package com.ttn.linksharing.entity;

import com.ttn.linksharing.enums.Visibility;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class Topic {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @OneToMany(mappedBy = "topic")
    private List<Resource> resource;



}
