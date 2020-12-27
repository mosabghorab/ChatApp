package com.example.chatapp.Part_2;


import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.chatapp.Activities.LoginActivity;
import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {


    @Rule
   public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);


    @Test
    public  void testIsVisible() {
        // this is a button.
        onView(ViewMatchers.withId(R.id.btn_login)).perform(click());
    }
}
