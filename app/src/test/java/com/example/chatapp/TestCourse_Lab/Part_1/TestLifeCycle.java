package com.example.chatapp.TestCourse_Lab.Part_1;

import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
public class TestLifeCycle {
    private Message message;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("BeforeClass");
    }

    @Before
    public void before() {
        System.out.println("Before");
        User currentUser = new User("mosab@gmail.com","Mosab Ghorab");
        message = new Message(5,"20/8/2020",currentUser.getEmail().equals("mosab@gmail.com"));
    }

    @Test
    public void test() {
        System.out.println("Test");
        assertFalse(message.isSender());
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


