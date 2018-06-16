package com.cherryperry.amiami.model.mongodb

import com.cherryperry.amiami.Configuration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.concurrent.TimeUnit

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [Configuration::class])
@DataMongoTest
class ItemRepositoryImplTest {

    @Autowired
    lateinit var itemRepository: ItemRepositoryImpl

    @Autowired
    lateinit var mongoOperations: MongoOperations

    @Before
    fun before() {
        mongoOperations.dropCollection(Item::class)
    }

    @After
    fun after() {
        mongoOperations.dropCollection(Item::class)
    }

    @Test
    fun testSingleItemInsertion() {
        val item = Item("url", "name", "image", "100", "10", 1)
        itemRepository.compareAndSave(item)
        val items = itemRepository.items()
        assertEquals(1, items.size)
        assertTrue(items.contains(item))
    }

    @Test
    fun testTwoItemInsertion() {
        val item1 = Item("url1", "name", "image", "100", "10", 1)
        val item2 = Item("url2", "name", "image", "200", "20", 1)
        itemRepository.compareAndSave(item1)
        itemRepository.compareAndSave(item2)
        val items = itemRepository.items()
        assertEquals(2, items.size)
        assertTrue(items.contains(item1))
        assertTrue(items.contains(item2))
    }

    @Test
    fun testItemUpdateInsertion() {
        val item1 = Item("url1", "name", "image", "100", "10", 1)
        val item2 = Item("url1", "name", "image", "200", "20", 2)
        itemRepository.compareAndSave(item1)
        itemRepository.compareAndSave(item2)
        val items = itemRepository.items()
        assertEquals(1, items.size)
        assertTrue(items.contains(item2))
    }

    @Test
    fun testRemoveOther() {
        val item1 = Item("url1", "name", "image", "100", "10", 1)
        val item2 = Item("url2", "name", "image", "200", "20", 1)
        val item3 = Item("url3", "name", "image", "300", "30", 1)
        val item4 = Item("url4", "name", "image", "400", "40", 1)
        itemRepository.compareAndSave(item1)
        itemRepository.compareAndSave(item2)
        itemRepository.compareAndSave(item3)
        itemRepository.compareAndSave(item4)
        itemRepository.deleteOther(arrayListOf(item1.url, item2.url))
        val items = itemRepository.items()
        assertEquals(2, items.size)
        assertTrue(items.contains(item1))
        assertTrue(items.contains(item2))
    }

    @Test
    fun testLastModifiedUpdates() {
        val lastModified = itemRepository.lastModified
        val item = Item("url", "name", "image", "100", "10", 1)
        Thread.sleep(TimeUnit.SECONDS.toMillis(1))
        itemRepository.compareAndSave(item)
        assertTrue(itemRepository.lastModified > lastModified)
    }
}