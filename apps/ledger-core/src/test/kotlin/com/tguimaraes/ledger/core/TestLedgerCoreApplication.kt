package com.tguimaraes.ledger.core

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<LedgerCoreApplication>().with(TestcontainersConfiguration::class).run(*args)
}
