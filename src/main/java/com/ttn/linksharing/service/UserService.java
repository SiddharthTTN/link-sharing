package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static String UPLOAD_IMAGE_FOLDER = "/home/ttn/linksharing-attatchments/userimage/";

    @Autowired
    UserRepository userRepository;

    public void register(User user) {

        try {
            // Get the file and save it somewhere
            MultipartFile multipartFile = user.getUserImage();
            String fileName = multipartFile.getName();
            String filePath = UPLOAD_IMAGE_FOLDER + new Date().getTime() + fileName.replaceAll(" ", "-");
            multipartFile.transferTo(new File(filePath));
            user.setImagePath(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setIsAdmin(false);
        userRepository.save(user);
    }

    public User login(User user) throws Exception {
        User user1 = userRepository.userLogin(user.getUsername(), user.getPassword());
        if (user1 == null) throw new Exception(String.valueOf(user));
        return user1;
    }

    public void reset(User user) throws Exception {
        User user1 = userRepository.findByUsernameOrEmail(user.getUsername(), user.getUsername());
        user1.setPassword(user.getPassword());
        userRepository.save(user1);
    }

    public User getUser(User user) {
        if (validateEmail(user.getUsername()))
            return userRepository.findByEmailAndPassword(user.getUsername(), user.getPassword());
        else
            return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
    }

    public Boolean validateEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public User usernameExists(String username){
        return userRepository.findByUsername(username);
    }

    public User emailExists(String email){
        return userRepository.findByEmail(email);
    }


}