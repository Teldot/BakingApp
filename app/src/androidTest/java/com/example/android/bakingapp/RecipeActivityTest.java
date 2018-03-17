package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.bakingapp.ui.MainListActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Mauricio Torres
 * on 16/03/2018.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {
    private final static String INGREDIENT_NAME = " - " + "sifted cake flour";
    private final static String STEP_NAME = "Recipe Introduction";
    private final static int YELLOW_CAKE_RECIPE_POS = 2;
    private final static int INGREDIENT_INDEX = 0;
    private final static int STEP_INDEX = 0;


    @Rule
    public ActivityTestRule<MainListActivity> mainListActivityActivityTestRule =
            new ActivityTestRule<>(MainListActivity.class);

    @Before
    public void loadRecipeInfo() {
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(YELLOW_CAKE_RECIPE_POS, click()));

    }

    @Test
    public void recipeLoaded() {
        onView(withId(R.id.rv_recipe_ingredients_list)).check(
                matches(
                        withViewAtPosition(INGREDIENT_INDEX,
                                hasDescendant(allOf(
                                        withId(R.id.tv_ingredient_name),
                                        withText(INGREDIENT_NAME))))
                )
        );

        onView(withId(R.id.rv_recipe_steps_list)).check(
                matches(
                        withViewAtPosition(STEP_INDEX,
                                hasDescendant(allOf(
                                        withId(R.id.item_recipe_step_desc),
                                        withText(STEP_NAME)
                                )))
                )
        );

        onView(withId(R.id.rv_recipe_steps_list)).
                perform(RecyclerViewActions.actionOnItemAtPosition(STEP_INDEX, click()));

        onView(withId(R.id.tv_step_short_description)).check(matches(
                withText(STEP_NAME))
        );

        Espresso.pressBack();

    }


    public static Matcher<View> withViewAtPosition(final int position, final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
