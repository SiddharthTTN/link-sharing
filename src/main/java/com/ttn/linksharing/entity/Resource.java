package com.ttn.linksharing.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Data
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    User user;

    @ManyToOne
    private Topic topic;

    @OneToMany(mappedBy = "resource")
    private List<ResourceRating> resourceRatings = new ArrayList<>();
}
