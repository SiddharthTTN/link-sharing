package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static String UPLOAD_IMAGE_FOLDER = "/home/ttn/linksharing-attatchments/userimage/";

    @Autowired
    UserRepository userRepository;

    //To register the user
    public void register(User user) {
        try {
            // Get the file and save it somewhere
            MultipartFile multipartFile = user.getUserImage();
            String fileName = user.getUsername() + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String filePath = UPLOAD_IMAGE_FOLDER + fileName.replaceAll(" ", "-");
            multipartFile.transferTo(new File(filePath));
            user.setImagePath(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setIsAdmin(false);
        userRepository.save(user);
    }

    //To Update the user Profile
    public void updateProfile(User user) {

    }

    //To reset the password
    public void reset(User user) throws Exception {
        User user1 = userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername());
        user1.setPassword(user.getPassword());
        userRepository.save(user1);
    }

    //To fetch user at the time of login
    public User getUser(User user) {
        if (validateEmail(user.getUsername()))
            return userRepository.findByEmailAndPassword(user.getUsername(), user.getPassword());
        else
            return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
    }

    //Email Validation
    public Boolean validateEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //To Check whether the username exists or not
    public User usernameExists(String username) {
        return userRepository.findByUsername(username);
    }

    //To Check whether the email is already registered or not
    public User emailExists(String email) {
        return userRepository.findByEmail(email);
    }


}