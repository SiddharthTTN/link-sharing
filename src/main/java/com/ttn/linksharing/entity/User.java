package com.ttn.linksharing.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String password;

    @Transient
    private String confirmPassword;

    private String imagePath;

    @Transient
    MultipartFile userImage;

    private Boolean isAdmin=false;

    private Boolean isActive=true;
}
