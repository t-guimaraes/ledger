package com.tguimaraes.ledger.core.domain.exception

class InvalidAccountWithdrawAmountException :
    RuntimeException("Withdraw amount must be greater than zero")