package com.wywrot.todo.activity

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.wywrot.todo.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ToDoListActivityUiTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ToDoListActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanAfter() {
        Intents.release()
    }

    @Test
    fun getActionBar_checkIfDisplayed_checkTitle() {
        val textView = onView(
            allOf(
                withText("Yours TODOs"),
                withParent((withId(R.id.action_bar))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Yours TODOs")))
    }

    @Test
    fun getFabButton_checkIfDisplayed() {
        val imageButton = onView(
            allOf(
                withId(R.id.fabButton), withContentDescription("Button to add new todo item"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))
    }

    @Test
    fun getFabButton_checkIfDisplayedAndClick_checkIfNextActivityStarted() {
        val floatingActionButton = onView(
            allOf(
                withId(R.id.fabButton), withContentDescription("Button to add new todo item"),
                childAtPosition(childAtPosition(withId(android.R.id.content), 0), 1),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())
        intended(hasComponent(CreateToDoActivity::class.java.name))
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
