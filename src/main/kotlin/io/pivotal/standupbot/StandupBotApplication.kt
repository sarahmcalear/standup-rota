package io.pivotal.standupbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StandupBotApplication

fun main(args: Array<String>) {
    runApplication<StandupBotApplication>(*args)
}
