package com.tguimaraes.ledger.core.domain.exception

class InvalidTransferAmountException :
    RuntimeException("Transfer amount must be greater than zero")