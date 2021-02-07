package com.wywrot.todo

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.firebase.FirebaseApp
import com.wywrot.todo.activity.ToDoListActivity
import com.wywrot.todo.utils.Status
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ToDoListActivityTest {
    private var activity: ToDoListActivity? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        activity = Robolectric.buildActivity(ToDoListActivity::class.java)
            .create()
            .resume()
            .get()

        FirebaseApp.initializeApp(activity?.applicationContext!!)
    }

    @Test
    @Throws(Exception::class)
    fun activity_shouldNotBeNull() {
        assertNotNull(activity)
    }

    @Test
    fun fetchData_checkIfFirstStatusIsLoading() {
        val liveData = activity?.viewModel?.downloadToDoItems(false)
        liveData?.observe(mockLifecycleOwner(), {
            assertThat(it.status, `is`(Status.LOADING))
        })
    }

    private fun mockLifecycleOwner(): LifecycleOwner {
        val owner: LifecycleOwner = mock(LifecycleOwner::class.java)
        val lifecycle = LifecycleRegistry(owner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        `when`(owner.lifecycle).thenReturn(lifecycle)
        return owner
    }
}