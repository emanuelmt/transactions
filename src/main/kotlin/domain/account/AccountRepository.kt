package dev.emanuelmt.domain.account

interface AccountRepository {
    suspend fun save(account: AccountEntity)
}