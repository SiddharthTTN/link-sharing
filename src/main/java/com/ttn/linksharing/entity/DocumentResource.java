package com.ttn.linksharing.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Data
@DiscriminatorValue("Document")
public class DocumentResource extends Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String path;

    @Transient
    MultipartFile documentResource;
}
