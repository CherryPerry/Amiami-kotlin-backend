package com.cherryperry.amiami.controller

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RunWith(SpringJUnit4ClassRunner::class)
class DataControllerTest {

    private val itemRepository = DataControllerItemRepositoryTestImpl()

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(DataController(itemRepository))
        .alwaysDo<StandaloneMockMvcBuilder>(MockMvcResultHandlers.print())
        .build()

    private val lastModifiedString = DateTimeFormatter.RFC_1123_DATE_TIME
        .withZone(ZoneId.of("UTC"))
        .format(Instant.ofEpochMilli(itemRepository.lastModified()))

    @Test
    fun testDefaultResponse() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/data"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LAST_MODIFIED, lastModifiedString))
            .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.content().string("[{\"url\":\"1\",\"name\":\"2\",\"image\":\"3\",\"price\":\"4\",\"discount\":\"5\",\"time\":6},{\"url\":\"7\",\"name\":\"8\",\"image\":\"9\",\"price\":\"1\",\"discount\":\"2\",\"time\":3}]"))
    }

    @Test
    fun testNotModifiedHeader() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/data")
            .header(HttpHeaders.IF_MODIFIED_SINCE, lastModifiedString))
            .andExpect(MockMvcResultMatchers.status().isNotModified)
    }
}