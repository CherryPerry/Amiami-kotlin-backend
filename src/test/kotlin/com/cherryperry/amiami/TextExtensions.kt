package com.cherryperry.amiami

fun readResourceToString(file: String) = App::class.java.classLoader
        .getResourceAsStream(file)
        .bufferedReader()
        .use { it.readText() }
