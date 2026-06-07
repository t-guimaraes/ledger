package com.tguimaraes.ledger.core.config

import com.tguimaraes.ledger.core.domain.service.TransferDomainService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfig {

    @Bean
    fun transferDomainService(): TransferDomainService =
        TransferDomainService()
}