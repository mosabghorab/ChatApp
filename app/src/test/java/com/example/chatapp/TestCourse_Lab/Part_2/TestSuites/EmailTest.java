package com.example.chatapp.TestCourse_Lab.Part_2.TestSuites;

import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.Utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EmailTest {
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
    public void validateEmail() {
        System.out.println("validate email");
        assertTrue(utils.validateEmail(user.getEmail()));
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
