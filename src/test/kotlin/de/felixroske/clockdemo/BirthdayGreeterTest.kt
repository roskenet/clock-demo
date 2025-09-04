package de.felixroske.clockdemo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId

class BirthdayGreeterTest {

    @Test
    fun testBirthdayGreeter_True() {
        val fixedClock = Clock.fixed(Instant.parse("2025-01-09T09:00:00Z"), ZoneId.of("Pacific/Honolulu"))
        val customer = Customer("Elvis", LocalDate.of(1935, Month.JANUARY, 8))
        val greeter = BirthdayGreeter(fixedClock)

        val result = greeter.greet(customer)

        assertThat(result).isEqualTo("Happy Birthday, Elvis!")
    }
}