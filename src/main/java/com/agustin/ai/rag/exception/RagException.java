package com.agustin.ai.rag.exception;

public class RagException extends RuntimeException{
    protected RagException(String message){
        super(message);
    }

    protected RagException(String message, Throwable cause){
        super(message, cause);
    }
}
