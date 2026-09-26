package br.com.orbitapi.exceptions;

public class RegistroHistoricoNaoEncontradoException extends RuntimeException {

    public RegistroHistoricoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}