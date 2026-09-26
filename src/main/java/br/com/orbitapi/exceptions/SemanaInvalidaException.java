package br.com.orbitapi.exceptions;

public class SemanaInvalidaException extends RuntimeException {

    public SemanaInvalidaException(String mensagem) {
        super(mensagem);
    }
}