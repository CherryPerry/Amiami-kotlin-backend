package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.readResourceToString
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
class ItemCrawlerTest {

    companion object {
        const val URL_TEMPLATE_LIST_MULTI = "http://localhost/list/multi?page="
        const val URL_TEMPLATE_LIST_SINGLE = "http://localhost/list/single?page="
        const val URL_TEMPLATE_LIST_EMPTY = "http://localhost/list/empty?page="
        const val URL_TEMPLATE_ITEM_MULTI_1 = "http://www.amiami.com/top/detail/detail?scode=FIGURE-005846-R172"
        const val URL_TEMPLATE_ITEM_MULTI_2 = "http://www.amiami.com/top/detail/detail?scode=FIGURE-005846-R170"
        const val URL_TEMPLATE_ITEM_SINGLE = "http://localhost/item/2"
        const val URL_TEMPLATE_ITEM_EMPTY = "http://localhost/item/3"
    }

    private lateinit var itemCrawler: ItemCrawler

    @Before
    fun before() {
        val api = object : AmiamiHtmlAPI {
            override fun htmlPage(url: String): Single<String> {
                return Single.just(readResourceToString(when (url) {
                    URL_TEMPLATE_LIST_MULTI + "1" -> "crawler/crawler_page_list_multi_1.html"
                    URL_TEMPLATE_LIST_MULTI + "2" -> "crawler/crawler_page_list_multi_2.html"
                    URL_TEMPLATE_LIST_EMPTY + "1" -> "crawler/crawler_page_list_empty.html"
                    URL_TEMPLATE_LIST_SINGLE + "1" -> "crawler/crawler_page_list_single.html"
                    URL_TEMPLATE_ITEM_EMPTY -> "crawler/crawler_page_item_empty.html"
                    URL_TEMPLATE_ITEM_SINGLE -> "crawler/crawler_page_item_single.html"
                    URL_TEMPLATE_ITEM_MULTI_1 -> "crawler/crawler_page_item_multi_1.html"
                    URL_TEMPLATE_ITEM_MULTI_2 -> "crawler/crawler_page_item_multi_2.html"
                    else -> throw IllegalArgumentException(url)
                }))
            }
        }
        itemCrawler = ItemCrawler(api)
    }

    @Test
    fun crawlListEmpty() {
        itemCrawler.crawlLists(URL_TEMPLATE_LIST_EMPTY)
            .test()
            .await()
            .assertNoValues()
    }

    @Test
    fun crawlListSingle() {
        val list = arrayOf(
            HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=TOY-010756-R",
                image = "http://img.amiami.jp/images/product/thumbnail/182/TOY-010756.jpg",
                price = "4,780 JPY",
                discount = ""
            ))
        itemCrawler.crawlLists(URL_TEMPLATE_LIST_SINGLE)
            .test()
            .await()
            .assertValues(*list)
    }

    @Test
    fun crawlListMulti() {
        val list = arrayOf(
            HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=FIG-MOE-7556-R",
                image = "http://img.amiami.jp/images/product/thumbnail/124/FIG-MOE-7556.jpg",
                price = " 2,240 JPY ~",
                discount = "15% - 31% OFF"
            ),
            HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=FIGURE-011757-R",
                image = "http://img.amiami.jp/images/product/thumbnail/152/FIGURE-011757.jpg",
                price = " 6,980 JPY",
                discount = "46% OFF"
            ),
            HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=FIGURE-011480-R",
                image = "http://img.amiami.jp/images/product/thumbnail/152/FIGURE-011480.jpg",
                price = " 10,980 JPY",
                discount = "26% OFF"
            ),
            HtmlParser.ListItem(
                url = "http://www.amiami.com/top/detail/detail?gcode=FIG-MOE-7792-R",
                image = "http://img.amiami.jp/images/product/thumbnail/124/FIG-MOE-7792.jpg",
                price = " 2,980 JPY",
                discount = "9% OFF"
            ))
        itemCrawler.crawlLists(URL_TEMPLATE_LIST_MULTI)
            .test()
            .await()
            .assertValues(*list)
    }

    @Test
    fun crawlItemEmpty() {
        val item = HtmlParser.ListItem(
            url = URL_TEMPLATE_ITEM_EMPTY,
            image = "",
            price = "",
            discount = ""
        )
        itemCrawler.crawlItem(item)
            .test()
            .await()
            .assertNoValues()
    }

    @Test
    fun crawlItemSingle() {
        val item = HtmlParser.ListItem(
            url = URL_TEMPLATE_ITEM_SINGLE,
            image = "image",
            price = "1",
            discount = "1"
        )
        val list = arrayOf(
            HtmlParser.Item(
                url = URL_TEMPLATE_ITEM_SINGLE,
                name = "(Pre-owned ITEM:A/BOX:B)Cu-poche - Fate/Grand Order: Saber/Altria Pendragon Posable Figure(Released)",
                image = "image",
                price = "5,980 JPY",
                discount = "4%OFF"
            ))
        itemCrawler.crawlItem(item)
            .test()
            .await()
            .assertValues(*list)
    }

    @Test
    fun crawlItemMulti() {
        val item = HtmlParser.ListItem(
            url = URL_TEMPLATE_ITEM_MULTI_1,
            image = "image",
            price = "1",
            discount = "1"
        )
        val list = arrayOf(
            HtmlParser.Item(
                url = URL_TEMPLATE_ITEM_MULTI_1,
                name = "(Pre-owned ITEM:B/BOX:B)Saki Zenkoku Hen - Hajime Kunihiro 1/7 Complete Figure [HobbyJAPAN Exclusive](Released)",
                image = "image",
                price = "5,980 JPY",
                discount = "33%OFF"
            ),
            HtmlParser.Item(
                url = URL_TEMPLATE_ITEM_MULTI_2,
                name = "(Pre-owned ITEM:A/BOX:B)Saki Zenkoku Hen - Hajime Kunihiro 1/7 Complete Figure [HobbyJAPAN Exclusive](Released)",
                image = "image",
                price = "7,480 JPY",
                discount = "16%OFF"
            ))
        itemCrawler.crawlItem(item)
            .test()
            .await()
            .assertValues(*list)
    }
}