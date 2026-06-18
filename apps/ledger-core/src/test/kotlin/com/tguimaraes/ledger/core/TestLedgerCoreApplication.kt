package com.tguimaraes.ledger.core

import org.springframework.boot.fromApplication
import org.springframework.boot.with
import org.testcontainers.utility.TestcontainersConfiguration

fun main(args: Array<String>) {
    fromApplication<LedgerCoreApplication>().with(TestcontainersConfiguration::class).run(*args)
}
