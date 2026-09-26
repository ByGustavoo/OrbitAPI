package br.com.orbitapi.exceptions;

public class PeriodoInvalidoException extends RuntimeException {

    public PeriodoInvalidoException(String mensagem) {
        super(mensagem);
    }
}