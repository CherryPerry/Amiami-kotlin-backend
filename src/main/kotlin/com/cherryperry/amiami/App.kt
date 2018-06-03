package com.cherryperry.amiami

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class App {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(App::class.java, *args)
        }
    }
}