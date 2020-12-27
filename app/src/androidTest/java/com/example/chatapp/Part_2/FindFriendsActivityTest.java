package com.example.chatapp.Part_2;
import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;


import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.chatapp.Activities.FindFreindsActivity;
import com.example.chatapp.Activities.MainActivity;
import com.example.chatapp.Activities.SplashActivity;
import com.example.chatapp.Models.User;
import com.example.chatapp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class FindFriendsActivityTest {

    private static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }

                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }

                return false;
            }
        };
    }


    @Rule
   public ActivityScenarioRule<FindFreindsActivity> activityRule = new ActivityScenarioRule<>(FindFreindsActivity.class);


    @Test
    public  void testIsVisible() {
        // this is recycler view.
        onView(ViewMatchers.withId(R.id.rv_users)).check(matches(isDisplayed()));
        // this is adapter view.
//        onData(allOf(is(instanceOf(User.class))),withAdaptedData(User("mosab")))
//                .perform(click());
    }

}
