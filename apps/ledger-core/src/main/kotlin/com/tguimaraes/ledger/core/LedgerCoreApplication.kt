package com.tguimaraes.ledger.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LedgerCoreApplication

fun main(args: Array<String>) {
	runApplication<com.tguimaraes.ledger.core.LedgerCoreApplication>(*args)
}
