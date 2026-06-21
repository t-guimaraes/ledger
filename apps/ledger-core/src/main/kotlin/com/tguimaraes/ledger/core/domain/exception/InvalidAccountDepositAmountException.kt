package com.tguimaraes.ledger.core.domain.exception

class InvalidAccountDepositAmountException :
    RuntimeException("Deposit amount must be greater than zero")