# The java.time.Clock Abstraction Pattern

![Clock Abstraction Pattern](./assets/duke_back_to_the_future_0.webp)

Our task is to write a BirthdayGreeter:

## The quick and easy implementation

```kotlin
@Component
class BirthdayGreeter {
    fun greet(customer: Customer): String {

        val today = LocalDate.now()

        if (customer.birthday.month == today.month &&
            customer.birthday.dayOfMonth == today.dayOfMonth)
            return "Happy Birthday, ${customer.name}!"
        else
            return "Hello, ${customer.name}!"

    }
}
```

How to test it:

```kotlin
@Test
fun testBirthdayGreeter_True() {
    val customer = Customer("Elvis", LocalDate.of(1935, Month.JANUARY, 8))
    val greeter = BirthdayGreeter()

    val result = greeter.greet(customer)

    assertThat(result).isEqualTo("Happy Birthday, Elvis!")
}
```

To make the test green we need to adjust Elvis' birthday.

### Why `LocalDate.now()` is a Problem in Tests

- **Determinism**  
  - Tests depend on real time → results vary by day or even by the hour  
  - Leads to *Heisenbugs* (pass today, fail tomorrow)

- **Repeatability**  
  - Same test doesn’t always yield same result  
  - Flaky tests = broken CI/CD trust

- **Controllability**  
  - You can’t set the “current date” in tests  
  - Hard to simulate edge cases (Dec 31, leap years, midnight)

- **Single Responsibility**  
  - Method mixes *time calculation* with *business logic*  
  - Split responsibilities for clarity and testability

- **Dependency Inversion** *(optional but important)*  
  - Code depends on low-level API (`.now()`)  
  - Better: inject an abstract `Clock` as the time source

## Introducing the Clock abstraction

We configure a clock:

```kotlin
@Configuration
class ClockConfiguration {

    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }

}
```

and use CTOR injection:

```kotlin
@Component
class BirthdayGreeter(val clock: Clock) {
    fun greet(customer: Customer): String {

        val today = LocalDate.now(clock)
        
```

Then we adjust the test:

```kotlin
    @Test
    fun testBirthdayGreeter_True() {
        val fixedClock = Clock.fixed(Instant.parse("2025-01-08T09:00:00Z"), ZoneId.of("Europe/Berlin"))
        val customer = Customer("Elvis", LocalDate.of(1935, Month.JANUARY, 8))
        val greeter = BirthdayGreeter(fixedClock)

        val result = greeter.greet(customer)

        assertThat(result).isEqualTo("Happy Birthday, Elvis!")
    }
```

### Fun with `Clock`s

import java.time.*

Clock baseClock = Clock.systemUTC()
LocalTime.now(baseClock)

Clock threeMinutesLate = Clock.offset(baseClock, Duration.ofMinutes(3))

### We travel in Space

```kotlin
val fixedClock = Clock.fixed(Instant.parse("2025-01-09T09:00:00Z"), ZoneId.of("Pacific/Honolulu"))
```

### Closing Thoughts: When (Not) to Use `.now()`

- Direct calls like `LocalDate.now()`, `Instant.now()`, `ZonedDateTime.now()`  
  → Hidden dependency on a global, uncontrollable resource  

- **In tests**: non-deterministic, flaky, unreliable  
- **In business logic**: can even produce wrong results  
  (time zones, DST, midnight boundaries...)

### When is `.now()` acceptable?

- **Trivial apps** → Hello World, one-off scripts  
- **Debugging/Logging** → capturing real execution time  
- **Throwaway code** → prototypes, quick POCs  

In all other cases: **use `Clock`**

### Why `Clock` pays off

1. **Clarity** – makes time dependency explicit  
2. **Testability** – deterministic & reproducible tests  
3. **Clean architecture** – respects SRP & Dependency Inversion  
4. **Future-proof** – leap years & time zone edge cases will come

### This is Nothing New 🚀

- We already use **abstractions for external resources** every day:
    - Database access → via `DataSource`
    - In production: points to the real DB
    - In tests: points to a differnt configuration DB (e.g. H2, Testcontainers, Docker-Postgres)

- Nobody connects to the "real" production DB in a unit test, right?  

- **Time is just another external resource**
    - In production: `Clock.systemDefaultZone()`
    - In tests: `Clock.fixed(...)` or `Clock.offset(...)`

Treat *time* like you treat your *database connection*  
