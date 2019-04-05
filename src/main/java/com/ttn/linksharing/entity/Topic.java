package com.ttn.linksharing.entity;

import com.ttn.linksharing.enums.Visibility;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"topicName", "user"}))
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String topicName;

    @OneToOne
    User user;

    Visibility visibility;

    @OneToMany(mappedBy = "topic")
    private List<Resource> resource;

}
