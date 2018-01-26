package io.pivotal.standupbot

import org.junit.Test
import reactor.core.publisher.Flux
import org.assertj.core.api.Assertions.*
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.toFlux

class StandupRotationTest {

    @Test
    fun `given a single person it should assign them to standup`() {
        val person = Person()
        val single = Flux.fromIterable(listOf(person))

        val standup = StandupRotation()

        standup.process(single)
        val result = standup.pairs().blockFirst()
        assertThat(result[0].first).isEqualTo(person)
    }

    @Test
    fun `given two people it should assign them both to standup`() {
        val person1 = Person()
        val person2 = Person()

        val double = Flux.fromIterable(listOf(person1, person2))

        val standup = StandupRotation()
        val result = standup.process(double).blockFirst()
        assertThat(result.first).isEqualTo(person1)
        assertThat(result.second).isEqualTo(person2)
    }

    @Test
    fun `given three people it should assign the two with the highest counts`() {
        val people = listOf(Person(1), Person(2), Person(3))
        val rotation = Flux.fromIterable(people)

        val standup = StandupRotation()
        val result = standup.process(rotation).blockFirst()

        assertThat(result.toList()).contains(people[2])
        assertThat(result.toList()).contains(people[1])
    }


}

class StandupRotation {

    private val processor = EmitterProcessor.create<List<Pair<Person, Person>>>()

    fun process(people: Flux<Person>): Flux<Pair<Person, Person>> {
        return people.collectList().map { x ->
            val sorted = x.sortedBy { (count) -> count }.reversed()
            val pair = when (sorted.size) {
                1 -> Pair(sorted[0], sorted[0])
                else -> Pair(sorted[0], sorted[1])

            }
            processor.sink().next(listOf(pair))
            pair
        }.toFlux()
    }

    fun pairs(): Flux<List<Pair<Person, Person>>> {
        return processor
    }
}

data class Person(val count: Int = 0) {

}
