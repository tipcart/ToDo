package com.wywrot.todo

import com.wywrot.todo.utils.Utils
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class UtilsTest {

    @Test
    fun getTimeInMillis_checkIfConvertedProperly() {
        val millis = 1612656414445L
        val date = Utils.getDateTime(millis)
        assertThat(date, `is`("02/07/2021"))
    }
}