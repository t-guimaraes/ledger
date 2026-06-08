# ADR-003 — Double-Entry Ledger (Core Finance Model)

## Status
Accepted

## Context

Modelos tradicionais de saldo como:
```
account.balance += amount
```

são perigosos em sistemas financeiros porque:

- não garantem auditabilidade
- não permitem reconstrução histórica confiável
- são vulneráveis a concorrência
- dificultam reconciliação contábil

Precisamos de um modelo equivalente ao usado por bancos reais.

---

## Decision

Adotar o modelo de **Double-Entry Accounting Ledger** como núcleo do domínio financeiro.

---

## Core Principle

> Every financial movement must have equal debit and credit.

---

## Data Model

### Transaction (Aggregate Root)

Representa uma operação financeira completa:

- Transfer
- Payment
- Adjustment

---

### Ledger Entries

Toda Transaction gera ao menos 2 entries:

| Account | Type  | Amount |
|--------|------|--------|
| A      | DEBIT | 100.00 |
| B      | CREDIT| 100.00 |

---

## Invariant principal
```
SUM(debits) == SUM(credits)
```
Esse invariant deve ser garantido pelo domínio.

---

## Flow de execução

1. UseCase valida regras de negócio
2. Cria Transaction
3. Gera Ledger Entries (debit/credit)
4. Valida invariant
5. Persiste tudo em uma transação atômica
6. Commit

---

## Properties

### Imutabilidade
- entries nunca são alteradas
- correções são novas transactions

---

### Auditabilidade
- qualquer saldo pode ser reconstruído a partir do ledger

---

### Consistência
- domínio garante invariants financeiros

---

## Consequences

### ✔ Benefícios

- modelo auditável (nível bancário)
- consistência forte
- rastreabilidade completa
- elimina inconsistência de saldo
- facilita debugging e reconciliação

---

### ⚠ Trade-offs

- maior complexidade que modelo simples de balance
- mais tabelas e joins
- curva de aprendizado maior
- exige disciplina no domínio

---

## Relationship with Hexagonal Architecture

- Domain: regras puras do ledger
- Application: orquestração via UseCases
- Adapters: DB, API, messaging

O ledger deve ser completamente independente de infraestrutura.

---

## Conclusion

Double-entry ledger é o modelo correto para sistemas financeiros sérios, garantindo consistência, auditabilidade e confiabilidade.