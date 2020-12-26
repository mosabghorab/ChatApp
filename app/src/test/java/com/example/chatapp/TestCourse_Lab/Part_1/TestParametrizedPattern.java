package com.example.chatapp.TestCourse_Lab.Part_1;
import com.example.chatapp.Models.Message;
import com.example.chatapp.Models.User;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class TestParametrizedPattern {
    private Boolean isSender;
    private Boolean expectedResult;
    Message message;

    @Before
    public void initialize() {
        User currentUser = new User("mosab@gmail.com","Mosab Ghorab");
         message = new Message(5,"20/8/2020",currentUser.getEmail().equals("mosab@gmail.com"));
    }

    public TestParametrizedPattern(Boolean isSender, Boolean expectedResult) {
        this.isSender = isSender;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection values() {
        return Arrays.asList(new Object[][]{
                {true, false},
                {false, true},
                {false, false},
        });
    }

    @Test
    public void testIsMessageForCurrentUser() {
        assertEquals(expectedResult, isSender);
    }
}
