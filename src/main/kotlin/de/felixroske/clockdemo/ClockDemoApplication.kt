package de.felixroske.clockdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate

@SpringBootApplication
class ClockDemoApplication

data class Customer(val name: String, val birthday: LocalDate)

@Configuration
class ClockConfiguration {
    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone();
    }
}

@Component
class BirthdayGreeter(val clock: Clock) {
    fun greet(customer: Customer): String {

        val today = LocalDate.now(clock)

        if (customer.birthday.month == today.month &&
            customer.birthday.dayOfMonth == today.dayOfMonth)
            return "Happy Birthday, ${customer.name}!"
        else
            return "Hello, ${customer.name}!"

    }
}