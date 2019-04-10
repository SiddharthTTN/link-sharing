package com.ttn.linksharing.entity;

import com.ttn.linksharing.enums.Seriousness;
import com.ttn.linksharing.service.SubscriptionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"}))
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    User user;

    @OneToOne
    Topic topic;

    @Enumerated(EnumType.STRING)
    private Seriousness seriousness = Seriousness.VERY_SERIOUS;

    @Autowired
    @Transient
    SubscriptionService subscriptionService;


}
