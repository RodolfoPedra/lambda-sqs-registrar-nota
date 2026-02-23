package registranotausuario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<ApiGatewayRequest, GatewayResponse> {

    public GatewayResponse handleRequest(final ApiGatewayRequest request, final Context context) {

        NotaUsuario notaUsuario = new NotaUsuario(
            request.getQueryStringParameters().get("urlNota"), 
            Long.parseLong(request.getQueryStringParameters().get("idUsuario")) );
        try {
            System.
            return new GatewayResponse(output, headers, 200);
        } catch (IOException e) {
            return new GatewayResponse("{}", headers, 500);
        }
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
