# Autorizador de Transações

Este projeto implementa um autorizador de transações de cartão de crédito com diversas estratégias de autorização e controle de concorrência. Cada estratégia foi exposta por uma rota separada, permitindo testes independentes. Para o controle de transações simultâneas (L4), optei por utilizar o **Redlock** com Redis, garantindo que apenas uma transação por conta seja processada a cada instante.

## Tecnologias Utilizadas

- **Kotlin**
- **Ktor** (framework web)
- **Exposed** (ORM para Kotlin)
- **Redis** (para Redlock)
- **PostgreSQL** (banco de dados)
- **Docker & Docker Compose**
  
## Endpoints Disponíveis
Cada estratégia de autorização possui uma rota dedicada. A seguir, os exemplos de chamadas via curl:

### 1. Criar Conta
**Endpoint: POST /account**

**Descrição:** Cria uma nova conta com base no nome informado e retorna o **accountId** que será utilizado nas outras requisições
```
curl --request POST \
  --url http://127.0.0.1:8080/account \
  --header 'Content-Type: application/json' \
  --data '{
    "name": "Customer Name"
}'
```

### 2. Atualizar Saldo da Carteira
**Endpoint: PATCH /wallet/balance**

**Descrição:** Atualiza o saldo da carteira para um tipo de benefício específico.
```
curl --request PATCH \
  --url http://127.0.0.1:8080/wallet/balance \
  --header 'Content-Type: application/json' \
  --data '{
    "accountId": "491207a3-c354-4615-bdfc-8dcf0cc2a731",
    "type": "MEAL",
    "balance": 15000
}'
```

### 3. Autorização Simples de Transação (L1)
**Endpoint: POST /transaction/simple-authorization**

**Descrição:** Autoriza a transação utilizando o mapeamento de MCC para a categoria de benefício.
```
curl --request POST \
  --url http://127.0.0.1:8080/transaction/simple-authorization \
  --header 'Content-Type: application/json' \
  --data '{
    "account": "491207a3-c354-4615-bdfc-8dcf0cc2a731",
    "totalAmount": 1000,
    "mcc": "1231",
    "merchant": "MERCHANT NAME"
}'
```

### 4. Autorização com Fallback (L2)
**Endpoint: POST /transaction/fallback-authorization**

**Descrição:** Caso o saldo do benefício mapeado pelo MCC não seja suficiente, utiliza o saldo do CASH como fallback.
```
curl --request POST \
  --url http://127.0.0.1:8080/transaction/fallback-authorization \
  --header 'Content-Type: application/json' \
  --data '{
    "account": "491207a3-c354-4615-bdfc-8dcf0cc2a731",
    "totalAmount": 1000,
    "mcc": "5411",
    "merchant": "MERCHANT NAME"
}'
```

### 5. Autorização Dependente do Comerciante (L3)
**Endpoint: POST /transaction/merchant-authorization**

**Descrição:** Ajusta a categoria de benefício com base no nome do comerciante, caso não tenha um mapeamento definido para o nome, utiliza o mapeamento via MCC.
```
curl --request POST \
  --url http://127.0.0.1:8080/transaction/merchant-authorization \
  --header 'Content-Type: application/json' \
  --data '{
    "account": "491207a3-c354-4615-bdfc-8dcf0cc2a731",
    "totalAmount": 1000,
    "mcc": "1234",
    "merchant": "UBER EATS"
}'
```

### 6. Consulta do Saldo da Carteira
**Endpoint: GET /wallet/{accountId}**

**Descrição:** Retorna o status atual da carteira para a conta informada.
```
curl --request GET \
  --url http://127.0.0.1:8080/wallet/491207a3-c354-4615-bdfc-8dcf0cc2a731
```

## Controle de Concorrrência (L4)
Para evitar que múltiplas transações sejam processadas simultaneamente na mesma conta, o projeto utiliza o Redlock implementado com Redis. Essa abordagem garante que, ao iniciar uma transação, um lock é adquirido para a conta, impedindo que outra transação seja processada até que o lock seja liberado.

### Execução do Projeto
Execute o seguinte comando na raiz do projeto para iniciar os containers:
docker compose up --build
A aplicação será exposta na porta 8080 (acessível em http://127.0.0.1:8080).
