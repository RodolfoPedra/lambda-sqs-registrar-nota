package poupacompra.registranotausuario;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public record NotaUsuario(String urlNota, Long idUsuario) {

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JacksonException e) {
            throw new RuntimeException("Erro ao converter NotaUsuario para JSON", e);
        }
    }
}
