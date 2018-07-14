package com.cherryperry.amiami.model.update

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class ItemCrawler(
    private val api: AmiamiHtmlAPI
) {

    fun crawlLists(urlTemplate: String): Flowable<HtmlParser.ListItem> {
        val urlGenerator = { page: Int -> urlTemplate + page }
        // загружаем первую страницу категории
        return api.htmlPage(urlGenerator(1))
            .toFlowable()
            .flatMap { html ->
                val parser = HtmlParser(html)
                // определяем сколько страниц всего
                val pageCount = parser.parsePageCount()
                Flowable.range(1, Math.max(1, pageCount))
                    .flatMapSingle { page ->
                        if (page == 1) {
                            // результат первой страницы уже есть
                            Single.just(parser.parseList())
                        } else {
                            // остальные страницы придется загрузить
                            api.htmlPage(urlGenerator(page))
                                .map { html -> HtmlParser(html).parseList() }
                        }
                    }
            }
            .flatMap { Flowable.fromIterable(it) }
    }

    fun crawlItem(listItem: HtmlParser.ListItem): Flowable<HtmlParser.Item> {
        // для каждого элемента списка нужно загрузить свою страницу
        return api.htmlPage(listItem.url)
            .flatMap {
                val parser = HtmlParser(it)
                // возможно в этой одной ссылке несколько продуктов
                val list = parser.parseItemForOtherItems(listItem)
                if (list.isEmpty()) {
                    // если других продуктов нет, то сразу парсим в результат
                    Single.just(listOf(parser.parseItem(listItem) ?: HtmlParser.Item.NULL))
                } else {
                    // на одной странице нескольколько продуктов, нужна информация по каждому
                    Flowable.fromIterable(list)
                        .flatMapSingle { listItem ->
                            api.htmlPage(listItem.url)
                                .map { html ->
                                    HtmlParser(html).parseItem(listItem) ?: HtmlParser.Item.NULL
                                }
                        }
                        .toList()
                }
            }
            .flatMapObservable { Observable.fromIterable(it) }
            .filter { it != HtmlParser.Item.NULL }
            .toFlowable(BackpressureStrategy.BUFFER)
    }
}
