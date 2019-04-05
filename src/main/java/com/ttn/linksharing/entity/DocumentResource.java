package com.ttn.linksharing.entity;

import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class DocumentResource extends Resource {
    private String path;
}
