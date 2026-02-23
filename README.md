# lambda-sqs-java

Function Lambda para envio de URL de NFCe para processamento de dados e enfileiramento no SQS

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
Para Arch Linux e derivados, o SAM CLI encontra-se no AUR.

## Build local application

```bash
sam build
```

## Deploy local application

```bash
sam local start-api --env-vars env-vars.json
```

## Deploy AWS Lambda

Realizar build padrão no maven e upload de .jar no Lambda 
```bash 
mvn clean <install|package>
```

### Em Edição...
