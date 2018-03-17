package com.example.android.bakingapp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.test.ProviderTestCase2;
import android.view.View;

import com.example.android.bakingapp.data.provider.RecipeContentProvider;
import com.example.android.bakingapp.data.provider.RecipeContract;
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
public class MainListActivityTest extends ProviderTestCase2<RecipeContentProvider> {

    private final static String NUTELLA_PIE_RECIPE_NAME = "Nutella Pie";
    private final static String BROWNIES_RECIPE_NAME = "Brownies";
    private final static String YELLOW_CAKE_RECIPE_NAME = "Yellow Cake";
    private final static String CHEESECAKE_RECIPE_NAME = "Cheesecake";

    private final static int NUTELLA_PIE_RECIPE_POS = 0;
    private final static int BROWNIES_RECIPE_POS = 1;
    private final static int YELLOW_CAKE_RECIPE_POS = 2;
    private final static int CHEESECAKE_RECIPE_POS = 3;

    private final static int NO_RECIPES = 0;
    private final static int ONE_RECIPE = 1;

    public MainListActivityTest() {
        super(RecipeContentProvider.class, RecipeContract.AUTHORITY);
    }

    @Rule
    public ActivityTestRule<MainListActivity> mainListActivityActivityTestRule =
            new ActivityTestRule<>(MainListActivity.class);

    @Before
    @Override
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @Test
    public void recipesLoaded() {
        assertEquals("No recipes in DB", NO_RECIPES, getRecipeFromDB());

        onView(withId(R.id.rv_recipe_list)).check(
                matches(
                        withViewAtPosition(NUTELLA_PIE_RECIPE_POS,
                                hasDescendant(allOf(withId(R.id.item_recipe_name), withText(NUTELLA_PIE_RECIPE_NAME))))
                )
        );
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(NUTELLA_PIE_RECIPE_POS, click()));
        onView(withId(R.id.tv_recipe_name)).check(matches(withText(NUTELLA_PIE_RECIPE_NAME)));

        assertEquals("(1) recipe in DB", ONE_RECIPE, getRecipeFromDB());
        Espresso.pressBack();
        assertEquals("No recipes in DB", NO_RECIPES, getRecipeFromDB());

        onView(withId(R.id.rv_recipe_list)).check(
                matches(
                        withViewAtPosition(BROWNIES_RECIPE_POS,
                                hasDescendant(allOf(withId(R.id.item_recipe_name), withText(BROWNIES_RECIPE_NAME))))
                )
        );
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(BROWNIES_RECIPE_POS, click()));
        onView(withId(R.id.tv_recipe_name)).check(matches(withText(BROWNIES_RECIPE_NAME)));

        assertEquals("(1) recipe in DB", ONE_RECIPE, getRecipeFromDB());
        Espresso.pressBack();
        assertEquals("No recipes in DB", NO_RECIPES, getRecipeFromDB());

        onView(withId(R.id.rv_recipe_list)).check(
                matches(
                        withViewAtPosition(YELLOW_CAKE_RECIPE_POS,
                                hasDescendant(allOf(withId(R.id.item_recipe_name), withText(YELLOW_CAKE_RECIPE_NAME))))
                )
        );
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(YELLOW_CAKE_RECIPE_POS, click()));
        onView(withId(R.id.tv_recipe_name)).check(matches(withText(YELLOW_CAKE_RECIPE_NAME)));

        assertEquals("(1) recipe in DB", ONE_RECIPE, getRecipeFromDB());
        Espresso.pressBack();
        assertEquals("No recipes in DB", NO_RECIPES, getRecipeFromDB());

        onView(withId(R.id.rv_recipe_list)).check(
                matches(
                        withViewAtPosition(CHEESECAKE_RECIPE_POS,
                                hasDescendant(allOf(withId(R.id.item_recipe_name), withText(CHEESECAKE_RECIPE_NAME))))
                )
        );
        onView(withId(R.id.rv_recipe_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(CHEESECAKE_RECIPE_POS, click()));
        onView(withId(R.id.tv_recipe_name)).check(matches(withText(CHEESECAKE_RECIPE_NAME)));

        assertEquals("(1) recipe in DB", ONE_RECIPE, getRecipeFromDB());
        Espresso.pressBack();
        assertEquals("No recipes in DB", NO_RECIPES, getRecipeFromDB());
    }

    private int getRecipeFromDB() {
        int recNum = 0;
        ContentResolver provider = getContext().getContentResolver();
//        ContentResolver provider = getMockContentResolver();
//        ContentProvider provider = getProvider();
        Cursor cursor = provider.query(
                RecipeContract.RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            recNum = cursor.getCount();
            cursor.close();
        }
        return recNum;
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
