package br.com.orbitapi.exceptions;

public class AtividadeIndisponivelException extends RuntimeException {

    public AtividadeIndisponivelException(String mensagem) {
        super(mensagem);
    }
}