package com.ledger.ledger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LedgerCoreApplication

fun main(args: Array<String>) {
	runApplication<LedgerCoreApplication>(*args)
}
