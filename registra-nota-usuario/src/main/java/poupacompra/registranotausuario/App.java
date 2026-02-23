package poupacompra.registranotausuario;

import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SqsException;

/**
 * Handler for requests to Lambda function.
 */

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String SQS_QUEUE_URL = "SQS_QUEUE_URL";
    private static final String AWS_REGION = "AWS_REGION";
    
    private static final SqsClient SQS_CLIENT = SqsClient.builder()
                .region(Region.of(System.getenv(AWS_REGION)))
                .build();    

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request,
            final Context context) {

        var logger = context.getLogger();

        NotaUsuario notaUsuario = new NotaUsuario(
                request.getQueryStringParameters().get("urlNota"),
                Long.parseLong(request.getQueryStringParameters().get("idUsuario")));

        String sqsQueueUrl = Optional.ofNullable(System.getenv(SQS_QUEUE_URL))
                .orElseThrow(() -> new RuntimeException("SQS_QUEUE_URL não configurada"));

        logger.log("Enviando mensagem para SQS: " + notaUsuario.toJson());

        try {
             SQS_CLIENT.sendMessage(builder -> builder
                    .queueUrl(sqsQueueUrl)
                    .messageBody(notaUsuario.toJson()));

            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody(String.format("Mensagem enviada para SQS"));
            return response;
        } catch (SqsException e) {
            logger.log("Erro ao enviar mensagem para SQS: " + e.getMessage());
            return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody(e.getMessage());
        }
    }
}
