package com.ttn.linksharing.dto;

import com.ttn.linksharing.entity.Subscription;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionDTO {

    @Autowired
    SubscriptionService subscriptionService;


}
