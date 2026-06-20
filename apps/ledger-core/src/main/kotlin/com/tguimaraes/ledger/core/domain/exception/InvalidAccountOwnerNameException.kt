package com.tguimaraes.ledger.core.domain.exception

class InvalidAccountOwnerNameException :
    RuntimeException("Account owner name must not be blank")