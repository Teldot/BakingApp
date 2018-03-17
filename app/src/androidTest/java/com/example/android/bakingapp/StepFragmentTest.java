package com.example.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.ui.MainListActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Mauricio Torres
 * on 17/03/2018.
 */

@RunWith(AndroidJUnit4.class)
public class StepFragmentTest {
    private final static int CHEESECAKE_RECIPE_POS = 3;
    private final static int STEP_INDEX_0 = 0;
    private final static String STEP_NAME_0 = "Recipe Introduction";
    private final static String STEP_NAME_1 = "1. Starting prep.";


    @Rule
    public ActivityTestRule<MainListActivity> mainListActivityActivityTestRule =
            new ActivityTestRule<>(MainListActivity.class);

    @Before
    public void loadStepInfo() {
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(CHEESECAKE_RECIPE_POS, click()));
        onView(withId(R.id.rv_recipe_steps_list)).
                perform(RecyclerViewActions.actionOnItemAtPosition(STEP_INDEX_0, click()));

    }

    @Test
    public void stepLoaded(){
        onView(withId(R.id.tv_step_short_description)).check(matches(
                withText(STEP_NAME_0))
        );

        onView(withId(R.id.btn_next_step)).perform(click());

        onView(withId(R.id.tv_step_short_description)).check(matches(
                withText(STEP_NAME_1))
        );

        onView(withId(R.id.btn_previous_step)).perform(click());

        onView(withId(R.id.tv_step_short_description)).check(matches(
                withText(STEP_NAME_0))
        );
    }
}
