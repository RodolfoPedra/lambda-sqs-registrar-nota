# 📋 Comandos AWS CLI para LocalStack

> Coleção de comandos utilizados para criar e configurar serviços AWS no LocalStack para o projeto **Poupa Compra**.

---

## 📑 Índice

1. [API Gateway](#-api-gateway)
2. [SQS (Simple Queue Service)](#-sqs-simple-queue-service)
3. [IAM (Identity and Access Management)](#-iam-identity-and-access-management)
4. [Lambda](#-lambda)

---

## 📝 Notas
Os IDs apresentados são somente como exemplos. Toda execução de comandos CLI deve receber os suas respectivas
configurações, conforme input e output executados.

- **REST API ID**: `mlf3hdp6yn`
- **Root Resource ID**: `mlxvjk9jzo`
- **Resource ID (registrar-nota-usuario)**: `udcjnhgnw6`
- **SQS Queue URL**: `http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/registra-nfce`
- **Lambda Function Name**: `registra-nfce`
- **IAM Role**: `lambda-sqs-notas`


## 🌐 API Gateway

### 1. Criar REST API

```bash
awslocal apigateway create-rest-api \
  --name "gerencia-nfce" \
  --region eu-central-1 \
  --no-verify-ssl
```

<details>
<summary>📤 Exemplo de resposta</summary>

```json
{
    "id": "mlf3hdp6yn",
    "name": "gerencia-nfce",
    "createdDate": 1772112860.0,
    "apiKeySource": "HEADER",
    "endpointConfiguration": {
        "types": ["EDGE"],
        "ipAddressType": "ipv4"
    },
    "disableExecuteApiEndpoint": false,
    "rootResourceId": "mlxvjk9jzo"
}
```

</details>

### 2. Listar recursos da API

```bash
awslocal apigateway get-resources --rest-api-id mlf3hdp6yn
```

<details>
<summary>📤 Exemplo de resposta</summary>

```json
{
    "items": [
        {
            "id": "mlxvjk9jzo",
            "path": "/"
        }
    ]
}
```

</details>

### 3. Criar recurso (endpoint)

```bash
awslocal apigateway create-resource \
  --rest-api-id mlf3hdp6yn \
  --parent-id mlxvjk9jzo \
  --path-part "registrar-nota-usuario"
```

### 4. Configurar método HTTP

```bash
awslocal apigateway put-method \
  --rest-api-id mlf3hdp6yn \
  --resource-id udcjnhgnw6 \
  --http-method POST \
  --request-parameters "method.request.querystring.idUsuario=true,method.request.querystring.urlNota=true" \
  --authorization-type "NONE"
```

<details>
<summary>📤 Exemplo de resposta</summary>

```json
{
    "items": [
        {
            "id": "mlxvjk9jzo",
            "path": "/",
            "resourceMethods": {
                "POST": {
                    "httpMethod": "POST",
                    "authorizationType": "NONE",
                    "apiKeyRequired": false,
                    "requestParameters": {
                        "method.request.querystring.idUsuario": true,
                        "method.request.querystring.urlNota": true
                    },
                    "methodResponses": {}
                }
            }
        }
    ]
}
```

</details>

### 5. Configurar integração com Lambda

```bash
awslocal apigateway put-integration \
  --rest-api-id mlf3hdp6yn \
  --resource-id udcjnhgnw6 \
  --http-method POST \
  --integration-http-method POST \
  --request-parameters '{
    "integration.request.querystring.idUsuario": "method.request.querystring.idUsuario",
    "integration.request.querystring.urlNota": "method.request.querystring.urlNota"
  }' \
  --uri arn:aws:apigateway:eu-central-1:lambda:path/2015-03-31/functions/arn:aws:lambda:eu-central-1:000000000000:function:registra-nfce/invocations \
  --type AWS_PROXY \
  --passthrough-behavior WHEN_NO_MATCH
```

### 6. Deploy da API

```bash
awslocal apigateway create-deployment \
  --rest-api-id mlf3hdp6yn \
  --stage-name local
```

### 7. Testar endpoint

```bash
curl -X GET http://mlf3hdp6yn.execute-api.localhost.localstack.cloud:4566/local/test
```

---

## 📨 SQS (Simple Queue Service)

### 1. Criar fila

```bash
awslocal sqs create-queue --queue-name registra-nfce
```

### 2. Obter ARN da fila

```bash
awslocal sqs get-queue-attributes \
  --queue-url http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/registra-nfce \
  --attribute-names QueueArn
```

### 3. Receber mensagens da fila

```bash
awslocal sqs receive-message \
  --queue-url http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/registra-nfce \
  --attribute-names All \
  --message-attribute-names All
```

### 4. Consultar mensagens via HTTP

```bash
curl http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/registra-nfce/messages
```

---

## 🔐 IAM (Identity and Access Management)

### 1. Criar role para Lambda

```bash
awslocal iam create-role \
  --role-name lambda-sqs-notas \
  --region eu-central-1 \
  --assume-role-policy-document file:///home/rodolfo/projetos/poupacompra/poupa-compra-scrapping/lambda-sqs-java/policies/lambda-role-trust-policy.json
```

---

## ⚡ Lambda

### 1. Criar função Lambda

```bash
awslocal lambda create-function \
  --function-name registra-nfce \
  --runtime java21 \
  --zip-file fileb:///home/rodolfo/projetos/poupacompra/poupa-compra-scrapping/lambda-sqs-java/registra-nota-usuario/target/registrar-nota-usuario-1.0.jar \
  --handler poupacompra.registranotausuario.App::handleRequest \
  --role arn:aws:iam::000000000000:role/lambda-sqs-notas
```

### 2. Configurar variáveis de ambiente

```bash
awslocal lambda update-function-configuration \
  --function-name registra-nfce \
  --memory-size 512 \
  --environment "Variables={SQS_QUEUE_URL=http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/registra-nfce}"
```

### 3. Configurar destino de sucesso (SQS)

```bash
awslocal lambda put-function-event-invoke-config \
  --function-name registra-nfce \
  --destination-config '{
    "OnSuccess": {
      "Destination": "arn:aws:sqs:eu-central-1:000000000000:registra-nfce"
    }
  }'
```

---
