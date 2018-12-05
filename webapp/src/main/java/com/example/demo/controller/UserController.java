package com.example.demo.controller;

import com.amazonaws.services.s3.model.ObjectListing;
import com.example.demo.configuration.AmazonClient;
import com.example.demo.entity.User;
import com.example.demo.exception.MyException;
import com.example.demo.repository.AttachmentRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    AmazonClient amazonClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    MyException myException;

    @GetMapping
    public List<User> get() {
        return userRepository.findAll();
    }

    @PostMapping("/register")
    public User register(@RequestBody User user, HttpServletResponse response) {
        String pattern = "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b";
        if (!Pattern.matches(pattern, user.getEmail())) {
            myException.sendError(500, "Email is Not Valid", response);
        }
        User existUser = userRepository.findByEmail(user.getEmail());
        if (existUser == null) {
            user.setUsername(user.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return user;
        }

        myException.sendError(403, "User exist", response);
        return null;
    }

    @PostMapping("/userExist")
    public Boolean userExist(@RequestBody User user, HttpServletResponse response) {
        User existUser = userRepository.findByEmail(user.getEmail());
        if (existUser != null) {
            return true;
        }
        return false;
    }

    @PostMapping("/forget")
    public void forget(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
        String email = (String) payload.get("email");
        User user = userRepository.findByEmail(email);
        if (user == null) {
            myException.sendError(403, "User not exist", response);
        } else {
            amazonClient.publish(email);
        }
    }

    @PostMapping("/reset")
    public void reset(@RequestBody Map<String, Object> payload, HttpServletResponse response) {

    }

    @GetMapping("/clear")
    public void clear() {
        amazonClient.clearS3Bucket();
        File temp = new File("/temp");
        String[] files = temp.list();//取得当前目录下所有文件和文件夹
        for (String name : files) {
            File file = new File("/temp", name);
            file.delete();
        }
        attachmentRepository.deleteAll();
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }
}
