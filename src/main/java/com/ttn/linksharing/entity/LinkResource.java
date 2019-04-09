package com.ttn.linksharing.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@DiscriminatorValue("Link")
public class LinkResource extends Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;
}
