package com.example.chatapp.TestCourse.TestCases;

import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.Utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FunctionalityTest {
    User user;
    Utils utils;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("BeforeClass");
    }

    @Before
    public void before() {
        System.out.println("Before");
        utils = new Utils();
        user = new User("mosab@gmail.com","123-7890", "12345678", "Mosab Ghorab",null,null,"I am a mobile developer",System.currentTimeMillis(),true,false);
    }

    @Test
    public void validateMobile() {
        System.out.println("validate mobile");
        assertTrue(utils.validateMobileNumber(user.getMobile()));
    }

    @Test
    public void validateEmail() {
        System.out.println("validate email");
        assertTrue(utils.validateEmail(user.getEmail()));
    }

    @Test
    public void validatePassword() {
        System.out.println("validate password");
        assertTrue(utils.validatePassword(user.getPassword()));
    }

    @After
    public void after() {
        System.out.println("After");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("AfterClass");
    }
}
