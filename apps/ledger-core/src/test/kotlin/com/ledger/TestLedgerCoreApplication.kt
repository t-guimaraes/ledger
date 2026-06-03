package com.ledger

import com.ledger.core.LedgerCoreApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<LedgerCoreApplication>().with(TestcontainersConfiguration::class).run(*args)
}
