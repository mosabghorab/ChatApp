package com.example.chatapp.TestCourse_Lab.Part_2.TestMocking;


import com.example.chatapp.Models.Login.LogInTestInterface;
import com.example.chatapp.Presenters.LogInActivityPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.Silent.class)

public class LoginPresenterTest {

    LogInActivityPresenter logInActivityPresenter;

    @Mock
    LogInTestInterface logInTestInterface;


    @Before
    public  void setUp(){
        logInActivityPresenter = new LogInActivityPresenter(logInTestInterface);
    }

    @Test
    public void validateLoginInputsData() {
    logInActivityPresenter.validateData("mosab@gmail.com" , "mrmosab");
    Mockito.verify(logInTestInterface).sendLoginRequestToServer();
    }
}