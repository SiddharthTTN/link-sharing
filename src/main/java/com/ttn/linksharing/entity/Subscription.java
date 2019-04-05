package com.ttn.linksharing.entity;

import com.ttn.linksharing.enums.Seriousness;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user", "topic"}))
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    User user;

    @OneToOne
    Topic topic;

    private Seriousness seriousness;

}
