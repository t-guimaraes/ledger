package com.tguimaraes.ledger.core.adapter.outbound.cache

import com.tguimaraes.ledger.core.application.port.output.cache.IdempotencyCachePort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisIdempotencyCacheAdapter(
    private val redisTemplate: StringRedisTemplate
) : IdempotencyCachePort {

    override fun exists(
        key: String
    ): Boolean {

        return redisTemplate
            .hasKey(key)
    }

    override fun save(
        key: String
    ) {

        redisTemplate.opsForValue().set(
            key,
            "processed",
            Duration.ofHours(1)
        )
    }
}