package br.com.orbitapi.exceptions;

public class AtividadeDuplicadaException extends RuntimeException {

    public AtividadeDuplicadaException(String mensagem) {
        super(mensagem);
    }
}