package com.cherryperry.amiami.model.currency

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.web.client.HttpServerErrorException

@RunWith(SpringJUnit4ClassRunner::class)
class CurrencyRestClientTest {

    companion object {
        private const val ACCESS_TOKEN = "token"
    }

    private lateinit var server: MockWebServer
    private lateinit var currencyRestClient: CurrencyRestClient

    @Before
    fun before() {
        server = MockWebServer()
        currencyRestClient = CurrencyRestClient(server.url("/").toString())
    }

    @Test
    fun testSuccessRequest() {
        server.enqueue(MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody("""{"success":true,"timestamp":1519296206,"base":"USD","date":"2018-07-28","rates":{"AUD":1.566015,"CAD":1.560132,"CHF":1.154727}}"""))
        val result = currencyRestClient.currency(ACCESS_TOKEN)
        Assert.assertTrue(result.success)
        Assert.assertEquals("USD", result.base)
        Assert.assertEquals(1519296206L, result.timestamp)
        Assert.assertEquals("2018-07-28", result.date)
        Assert.assertEquals(3, result.rates!!.size)
        Assert.assertEquals(1.566015, result.rates!!["AUD"])
        val request = server.takeRequest()
        Assert.assertEquals("/latest?access_key=$ACCESS_TOKEN", request.path)
    }

    @Test
    fun testErrorRequest() {
        server.enqueue(MockResponse()
            .setResponseCode(HttpStatus.OK.value())
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .setBody("""{"success":false,"error":{"code":104,"info":"Your monthly API request volume has been reached. Please upgrade your plan."}}"""))
        val result = currencyRestClient.currency(ACCESS_TOKEN)
        Assert.assertFalse(result.success)
        Assert.assertNotNull(result.error)
        result.error?.let {
            Assert.assertEquals(104, it.code)
            Assert.assertEquals("Your monthly API request volume has been reached. Please upgrade your plan.", it.info)
        }
        val request = server.takeRequest()
        Assert.assertEquals("/latest?access_key=$ACCESS_TOKEN", request.path)
    }

    @Test(expected = HttpServerErrorException::class)
    fun testFailedRequest() {
        server.enqueue(MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()))
        currencyRestClient.currency(ACCESS_TOKEN)
    }

    @After
    fun after() {
        server.close()
    }
}
