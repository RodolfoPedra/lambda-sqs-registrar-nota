# lambda-sqs-java

Function Lambda em Java para receber requisições do AWS API Gateway contendo URL de NFCe e ID do usuário, e enfileirar os dados em formato JSON em uma fila do AWS SQS.

## 📋 Visão Geral

Este projeto faz parte do sistema **Poupa Compra** e é responsável por:
- Receber requisições HTTP POST via API Gateway
- Processar parâmetros `urlNota` e `idUsuario`
- Enfileirar os dados em formato JSON no SQS para processamento posterior

## 📁 Estrutura do Projeto

```
lambda-sqs-java/
├── registra-nota-usuario/       # Código fonte Java da aplicação Lambda
│   ├── src/main/java/           # Classes principais (App.java, NotaUsuario.java)
│   └── pom.xml                  # Configuração Maven
├── create-services-configs/     # Comandos AWS CLI para configurar LocalStack
│   └── COMMANDS.MD              # Documentação dos comandos
├── events/                      # Eventos de teste para AWS SAM
│   └── event.json               # Exemplo de evento API Gateway
├── policies/                    # Políticas IAM para o Lambda
│   └── lambda-role-trust-policy.json
├── template.yaml                # Template AWS SAM
├── env.json                     # Variáveis de ambiente (local)
└── env-exemplo.json             # Exemplo de configuração de variáveis
```

## ⚙️ Pré-requisitos

### Desenvolvimento Local

| Ferramenta | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 | JDK para compilação e execução |
| **Maven** | 3.x | Gerenciador de dependências e build |
| **LocalStack** | - | Emulador de serviços AWS localmente |
| **AWS CLI** | 2.x | Interface de linha de comando AWS |
| **AWS SAM CLI** | - | Para teste e debug do Lambda localmente |

> **Nota para Arch Linux**: O SAM CLI está disponível no AUR.

### Deploy na AWS

- Apenas o arquivo `.jar` da aplicação

## 🔧 Variáveis de Ambiente

| Variável | Descrição |
|----------|-----------|
| `SQS_QUEUE_URL` | URL da fila SQS de destino |
| `AWS_REGION` | Região AWS (ex: `eu-central-1`) |

Configure o arquivo `env.json` baseado no `env-exemplo.json`:

```json
{
  "registraNfce": {
    "SQS_QUEUE_URL": "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/registra-nfce",
    "AWS_REGION": "eu-central-1"
  }
}
```

## 🛠️ Build do Projeto

### Build com Maven (para deploy na AWS)

```bash
cd registra-nota-usuario
mvn clean package
```

O arquivo `.jar` será gerado em `registra-nota-usuario/target/registrar-nota-usuario-1.0.jar`

### Build com SAM (para desenvolvimento local)

```bash
sam build
```

## 🚀 Execução Local

### 1. Iniciar LocalStack

Certifique-se de que o LocalStack está em execução com os serviços SQS, Lambda e API Gateway.

### 2. Configurar serviços no LocalStack

Consulte os comandos em [create-services-configs/COMMANDS.MD](create-services-configs/COMMANDS.MD) para:
- Criar a fila SQS
- Criar a função Lambda
- Configurar o API Gateway
- Criar a role IAM

### 3. Executar com SAM

```bash
sam local start-api --env-vars env.json
```

### 4. Testar a API

```bash
curl -X POST "http://localhost:3000/registrar-nota-usuario?urlNota=http://example.com/nfce&idUsuario=12345"
```

### 5. Invocar diretamente com evento de teste

```bash
sam local invoke registraNfce --event events/event.json --env-vars env.json
```

## ☁️ Deploy na AWS

### Opção 1: Upload Manual

1. Faça o build do projeto:
   ```bash
   cd registra-nota-usuario
   mvn clean package
   ```

2. Realize o upload do arquivo `registrar-nota-usuario-1.0.jar` diretamente no console da AWS Lambda.

### Opção 2: Deploy com SAM

```bash
sam build
sam deploy --guided
```

## 📝 Formato da Requisição

**Endpoint:** `POST /registrar-nota-usuario`

**Query Parameters:**
| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `urlNota` | string | Sim | URL da nota fiscal eletrônica |
| `idUsuario` | number | Sim | ID do usuário |

**Exemplo de mensagem enviada ao SQS:**
```json
{
  "urlNota": "http://example.com/nota-fiscal-eletronica",
  "idUsuario": 4543564536
}
```

## 📄 Políticas IAM

O diretório `policies/` contém as políticas IAM necessárias para o Lambda:
- Permissões para envio de mensagens ao SQS
- Permissões para criação de logs no CloudWatch

## 📚 Referências

- [AWS SAM CLI - Instalação](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- [LocalStack](https://localstack.cloud/)
- [AWS Lambda Java](https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html)
