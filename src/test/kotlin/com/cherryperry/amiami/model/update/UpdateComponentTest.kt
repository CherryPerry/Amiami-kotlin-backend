package com.cherryperry.amiami.model.update

import com.cherryperry.amiami.model.mongodb.Item
import com.cherryperry.amiami.model.mongodb.ItemRepository
import com.cherryperry.amiami.model.push.PushService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.util.concurrent.TimeUnit

@RunWith(SpringJUnit4ClassRunner::class)
class UpdateComponentTest {

    private val itemRepository = object : ItemRepository {
        override fun items(): Collection<Item> = TODO()
        override fun compareAndSave(item: Item): Boolean = TODO()
        override fun deleteOther(ids: Collection<String>) = TODO()
        override val lastModified: Long = 0
    }
    private val pushService = object : PushService {
        override fun sendPushWithUpdatedCount(count: Int) = TODO()
        override fun sendPushWithUpdateCountToDevice(count: Int, token: String) = TODO()
    }
    private val restClient = object : AmiamiRestClient {
        override fun items(category: Int, perPage: Int, page: Int): AmiamiApiListResponse {
            Thread.sleep(TIMEOUT.second.toMillis(TIMEOUT.first * 2))
            return AmiamiApiListResponse()
        }
    }
    private val updateComponent = UpdateComponent(
        itemRepository = itemRepository,
        pushService = pushService,
        restClient = restClient,
        timeout = TIMEOUT,
    )

    @Test
    fun testInterruptedWhenStuck() {
        updateComponent.sync()
    }

    companion object {
        private val TIMEOUT = 10L to TimeUnit.SECONDS
    }

}
