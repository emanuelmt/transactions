package dev.emanuelmt.domain.application

interface RequestLock {
    fun tryLock(): Boolean
    fun releaseLock()
}

interface RequestLocker {
    fun getLock(accountId: String): RequestLock
}