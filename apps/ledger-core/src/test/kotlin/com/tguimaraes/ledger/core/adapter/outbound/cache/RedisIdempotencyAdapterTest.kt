package com.tguimaraes.ledger.core.adapter.outbound.cache

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class RedisIdempotencyAdapterTest {

    private val redisTemplate = mockk<StringRedisTemplate>()
    private val valueOperations = mockk<ValueOperations<String, String>>()

    private lateinit var adapter: RedisIdempotencyCacheAdapter

    @BeforeEach
    fun setup() {
        adapter = RedisIdempotencyCacheAdapter(redisTemplate)
    }

    @Test
    fun `should return true when key exists`() {

        every {
            redisTemplate.hasKey("key")
        } returns true

        assertTrue(adapter.exists("key"))

        verify(exactly = 1) {
            redisTemplate.hasKey("key")
        }
    }

    @Test
    fun `should return false when key does not exist`() {

        every {
            redisTemplate.hasKey("key")
        } returns false

        assertFalse(adapter.exists("key"))

        verify(exactly = 1) {
            redisTemplate.hasKey("key")
        }
    }

    @Test
    fun `should save idempotency key with ttl`() {

        every {
            redisTemplate.opsForValue()
        } returns valueOperations

        every {
            valueOperations.set(
                any(),
                any(),
                any<Duration>()
            )
        } just runs

        adapter.save("key")

        verify(exactly = 1) {
            valueOperations.set(
                "key",
                "processed",
                Duration.ofHours(1)
            )
        }
    }
}
