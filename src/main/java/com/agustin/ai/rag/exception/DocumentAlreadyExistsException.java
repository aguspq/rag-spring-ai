package com.agustin.ai.rag.exception;

public class DocumentAlreadyExistsException extends RagException {
    public DocumentAlreadyExistsException(String fileHash) {
        super("The document with hash: " + fileHash + " already exists");
    }
}
