package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.readResourceToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
class HtmlParserTest {

    @Test
    fun testPageCountNormal() {
        val html = readResourceToString("list_test_full.html")
        val parser = HtmlParser(html)
        assertEquals(79, parser.parsePageCount())
    }

    @Test
    fun testPageCountEmpty() {
        val html = readResourceToString("list_test_empty.html")
        val parser = HtmlParser(html)
        assertEquals(0, parser.parsePageCount())
    }

    @Test
    fun testListPageFull() {
        val html = readResourceToString("list_test_full.html")
        val parser = HtmlParser(html)
        val list = parser.parseList()
        assertEquals(50, list.size)
        assertEquals(HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=FIGURE-011480-R",
                image = "http://img.amiami.jp/images/product/thumbnail/152/FIGURE-011480.jpg",
                price = " 10,980 JPY",
                discount = "26% OFF"),
                list[0])
        assertEquals(HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=FIG-MOE-7556-R",
                image = "http://img.amiami.jp/images/product/thumbnail/124/FIG-MOE-7556.jpg",
                price = " 2,240 JPY ~",
                discount = "15% - 31% OFF"),
                list[2])
        assertEquals(HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=FIGURE-008099-R",
                image = "http://img.amiami.jp/images/product/thumbnail/143/FIGURE-008099.jpg",
                price = "5,980 JPY",
                discount = ""),
                list[4])
    }

    @Test
    fun testListPageEmpty() {
        val html = readResourceToString("list_test_empty.html")
        val parser = HtmlParser(html)
        val list = parser.parseList()
        assertEquals(0, list.size)
    }

    @Test
    fun test404() {
        val html = readResourceToString("404.html")
        val parser = HtmlParser(html)
        val list = parser.parseList()
        assertEquals(0, list.size)
    }

    @Test
    fun testItemMultiPriceSelectionOfMultiPrice() {
        val html = readResourceToString("item_test_multi_price_selection.html")
        val parser = HtmlParser(html)
        val item = HtmlParser.ListItem("", "", "", "")
        val list = parser.parseItemForOtherItems(item)
        assertEquals(3, list.size)
        assertEquals("http://www.amiami.com/top/detail/detail?scode=FIGURE-018243-R096", list[0].url)
        assertEquals("http://www.amiami.com/top/detail/detail?scode=FIGURE-018243-R098", list[1].url)
        assertEquals("http://www.amiami.com/top/detail/detail?scode=FIGURE-018243-R097", list[2].url)
    }

    @Test
    fun testItemMultiPriceSelectionOfSinglePrice() {
        val html = readResourceToString("item_test_same_price_selection.html")
        val parser = HtmlParser(html)
        val item = HtmlParser.ListItem("", "", "", "")
        val list = parser.parseItemForOtherItems(item)
        assertEquals(2, list.size)
        assertEquals("http://www.amiami.com/top/detail/detail?scode=FIGURE-026932-R005", list[0].url)
        assertEquals("http://www.amiami.com/top/detail/detail?scode=FIGURE-026932-R002", list[1].url)
    }

    @Test
    fun testItemMultiPriceSelectionOfNoMultiPrice() {
        val html = readResourceToString("item_test_simple.html")
        val parser = HtmlParser(html)
        val item = HtmlParser.ListItem("", "", "", "")
        val list = parser.parseItemForOtherItems(item)
        assertEquals(0, list.size)
    }

    @Test
    fun testItemSimple() {
        val html = readResourceToString("item_test_simple.html")
        val parser = HtmlParser(html)
        val item = HtmlParser.ListItem("", "", "", "")
        val result = parser.parseItem(item)
        assertEquals(HtmlParser.Item(
                url = "",
                name = "(Pre-owned ITEM:C/BOX:B)Touhou Project - Sealed Wizard \"Hijiri Byakuren\" 1/8 Complete Figure(Released)",
                image = "",
                price = "4,920 JPY",
                discount = "36%OFF"),
                result)
    }

    @Test
    fun testItemMultipleSamePrice() {
        val html = readResourceToString("item_test_same_price_selection.html")
        val parser = HtmlParser(html)
        val item = HtmlParser.ListItem("", "", "", "")
        val result = parser.parseItem(item)
        assertEquals(HtmlParser.Item(
                url = "",
                name = "(Pre-owned ITEM:A/BOX:B)figma - THE IDOLM@STER Million Live!: Miki Hoshii(Released)",
                image = "",
                price = "3,980 JPY",
                discount = "36%OFF"),
                result)
    }

    @Test
    fun testItemMultipleDifferentPrices() {
        val html = readResourceToString("item_test_multi_price_selection.html")
        val parser = HtmlParser(html)
        val item = HtmlParser.ListItem("", "", "", "")
        val result = parser.parseItem(item)
        assertEquals(HtmlParser.Item(
                url = "",
                name = "(Pre-owned ITEM:B+/BOX:B)Fate/kaleid liner Prisma Illya - Chloe Von Einzbern 1/7 Complete Figure [Monthly HobbyJAPAN 2015 Nov. & Dec. Issue Mail Order, Particular Shop Exclusive](Released)",
                image = "",
                price = "13,950 JPY",
                discount = ""),
                result)
    }

    @Test
    fun testItem404() {
        val html = readResourceToString("404.html")
        val parser = HtmlParser(html)
        val item = HtmlParser.ListItem("", "", "", "")
        val result = parser.parseItem(item)
        assertNull(result)
    }
}