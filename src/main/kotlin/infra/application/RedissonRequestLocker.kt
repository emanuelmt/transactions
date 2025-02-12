package dev.emanuelmt.infra.application

import dev.emanuelmt.domain.application.RequestLock
import dev.emanuelmt.domain.application.RequestLocker
import io.ktor.server.application.*
import io.ktor.util.*
import org.redisson.Redisson
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import java.util.concurrent.TimeUnit

val RequestLockerKey = AttributeKey<RequestLocker>("RedissonRequestLocker")

fun Application.configureRequestLocker() {
    val host = System.getenv("REDIS_HOST") ?: "redis"
    val port = System.getenv("REDIS_PORT") ?: "6379"

    this.attributes.put(RequestLockerKey, RedissonRequestLocker("redis://${host}:${port}"))
}

class RedissonRequestLocker(address: String) : RequestLocker {
    private val redisson: RedissonClient

    init {
        val config = Config()
        config.useSingleServer().address = address
        redisson = Redisson.create(config)
    }

    override fun getLock(accountId: String): RedissonLock {
        return RedissonLock(redisson.getLock("lock:account:$accountId"))
    }
}

class RedissonLock(private val lock: RLock) : RequestLock {
    override fun tryLock(): Boolean {
        return lock.tryLock(100, TimeUnit.MILLISECONDS)
    }

    override fun releaseLock() {
        return lock.unlock()
    }

}