package com.tguimaraes.ledger.core.domain.exception

class SameAccountTransferException :
    RuntimeException("Source and destination accounts must be different")