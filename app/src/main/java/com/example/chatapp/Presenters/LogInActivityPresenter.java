package com.example.chatapp.Presenters;

import com.example.chatapp.Models.Login.LogInTestInterface;
import com.example.chatapp.Models.Login.LogInTestPresenterInterface;

public class LogInActivityPresenter implements LogInTestPresenterInterface {
    LogInTestInterface logInTestInterface;

    public LogInActivityPresenter(LogInTestInterface logInTestInterface) {
        this.logInTestInterface = logInTestInterface;
    }


    @Override
    public void validateData(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            if(password.length() >= 8 && email.contains("@") && email.contains(".com")){
                logInTestInterface.sendLoginRequestToServer();
            }else{
                logInTestInterface.stopProcessAndShowError();
            }
        } else {
            logInTestInterface.stopProcessAndShowError();
        }

    }
}
