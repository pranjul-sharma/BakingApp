package in.p2psystem.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import in.p2psystem.bakingapp.adapters.RecipeListAdapter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class HomeActivityAdapterClickTest {
    private static final String RECIPE_NAME = "Brownies";
    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule
            = new ActivityTestRule<>(HomeActivity.class);
    private IdlingResource idlingResource;

    @Before
    public void registerIdlingResource() {
        idlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void recyclerItemClick_openRespectiveRecipeDetailActivity() {
        onView(withId(R.id.recycler_home)).perform(RecyclerViewActions.<RecipeListAdapter.ListViewHolder>actionOnItemAtPosition(1, click()));
        onView(withText(RECIPE_NAME)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
