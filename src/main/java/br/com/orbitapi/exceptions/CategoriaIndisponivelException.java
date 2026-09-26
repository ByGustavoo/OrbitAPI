package br.com.orbitapi.exceptions;

public class CategoriaIndisponivelException extends RuntimeException {

    public CategoriaIndisponivelException(String mensagem) {
        super(mensagem);
    }
}