package com.cherryperry.amiami.model.update

import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup

class HtmlParser(
    html: String
) {

    private val log = LogManager.getLogger(HtmlParser::class.java)
    private val doc = Jsoup.parse(html)

    fun parsePageCount(): Int {
        val nav = doc.selectFirst(".result_pagenavi")
        val elements = nav?.children()
        if (elements != null && elements.size >= 2) {
            val count = elements[elements.size - 2].text().replace(Regex("[\\[\\]]"), "").toIntOrNull()
            return count ?: 0
        }
        return 0
    }

    fun parseList(): List<ListItem> {
        val nodes = doc.select(".product_box")
        return nodes.mapNotNull {
            try {
                val divProductImg = it.select("div.product_img")?.first()
                val urlFull = divProductImg?.select("a")?.first()?.attr("href")
                val url = urlFull?.substring(0, urlFull.indexOf('&')) ?: return@mapNotNull null
                val image = divProductImg.select("a > img").first().attr("src")
                val discount = it.select("li.product_price > span.product_off")?.first()?.text() ?: ""
                val priceFull = it.select("li.product_price").first().text()
                val price = priceFull.replace(discount, "")
                ListItem(url, image, price, discount)
            } catch (exception: Exception) {
                log.error("Fail while parsing items list", exception)
                null
            }
        }
    }

    fun parseItemForOtherItems(item: ListItem): List<ListItem> {
        return try {
            doc.select(".icon_preowned")
                .mapNotNull {
                    val td = it.parent().parent()
                    val url = td.parent().attr("data-href")
                    item.copy(url = url)
                }
        } catch (exception: Exception) {
            log.error("Fail while parsing item $item", exception)
            emptyList()
        }
    }

    fun parseItem(item: ListItem): Item? {
        return try {
            val title = doc.selectFirst("#title > h2").textNodes().first().text()
            val li = doc.selectFirst("li.price")
            val discount = li.selectFirst(".off_price")?.text() ?: ""
            val price = li.textNodes().first().text()
            Item(item.url, title, item.image, price, discount)
        } catch (exception: Exception) {
            log.error("Fail while parsing item $item", exception)
            null
        }
    }

    data class ListItem(
        val url: String,
        val image: String,
        val price: String,
        val discount: String
    )

    data class Item(
        val url: String,
        val name: String,
        val image: String,
        val price: String,
        val discount: String
    ) {
        companion object {
            val NULL = Item("html://localhost", "NULL", "html://localhost", "", "")
        }
    }
}
