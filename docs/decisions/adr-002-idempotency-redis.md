# ADR-002 — Idempotency Key + Redis (Retry Safety Layer)

## Status
Accepted

## Context

Em sistemas distribuídos, requisições podem ser executadas mais de uma vez por motivos não funcionais, como:

- retries automáticos de HTTP clients
- timeouts seguidos de reenvio
- falhas de rede após execução parcial
- double submit no frontend
- reprocessamento de mensagens

Sem proteção, isso pode causar:

- duplicação de transfers
- inconsistência no ledger
- double spending
- múltiplas execuções de UseCases

Precisamos garantir **idempotência de operações financeiras**.

---

## Decision

Implementar um **Idempotency Key Store baseado em Redis**, garantindo execução única por operação.

---

## Design

### Chave de idempotência
```
idempotency:{accountId}:{operation}:{idempotencyKey}
```

Exemplo:
```
idempotency:acc-123:transfer:550e8400-e29b-41d4-a716-446655440000
```

---

## Flow de execução

### 1. Request chega com Idempotency-Key

O sistema tenta registrar execução:

```text
SET key "PROCESSING" NX EX 60
```

### 2. Casos possíveis
#### 🟢 Nova execução (key inexistente)
- Redis registra PROCESSING
- UseCase é executado
- Resultado é salvo
- Status atualizado para DONE

#### 🟡 Já em processamento
- outra requisição encontra PROCESSING
- sistema pode:
- aguardar
- ou retornar status pendente

#### 🔵 Já concluído
- retorna resultado cacheado
- evita reexecução do UseCase

---

## Consequências
### ✔ Benefícios
- proteção contra double execution
- segurança contra retry storms
- melhora de performance (cache de resultado)
- previsibilidade em APIs financeiras

### ⚠ Trade-offs
- dependência de Redis como componente crítico
- necessidade de TTL tuning (ex: 5–30 min)
- risco de chave stuck em estado PROCESSING
- complexidade adicional no fluxo de request

### Important Notes
- Idempotency NÃO substitui constraints do banco
- deve ser combinada com:
- unique constraints
- transações atômicas
- (futuro) outbox pattern

---
## Conclusion

Idempotency Key é uma camada de proteção de execução que garante segurança em ambientes distribuídos e não confiáveis.