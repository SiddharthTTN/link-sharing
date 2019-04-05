package com.ttn.linksharing.entity;

import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class LinkResource extends Resource {
    String url;
}
