package com.tguimaraes.ledger.core.adapters.output.redis

import com.tguimaraes.ledger.core.application.port.output.IdempotencyPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisIdempotencyAdapter(
    private val redisTemplate: StringRedisTemplate
) : IdempotencyPort {

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