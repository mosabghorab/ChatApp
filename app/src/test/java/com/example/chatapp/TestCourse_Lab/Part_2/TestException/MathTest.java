package com.example.chatapp.TestCourse_Lab.Part_2.TestException;

import com.example.chatapp.Models.User;
import com.example.chatapp.Utils.Utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MathTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("BeforeClass");
    }

    @Before
    public void before() {
        System.out.println("Before");
    }

    @Test(expected = ArithmeticException.class)
    public void divisionTest() {
        int num = 0;
        assertEquals(2,6/num);
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
