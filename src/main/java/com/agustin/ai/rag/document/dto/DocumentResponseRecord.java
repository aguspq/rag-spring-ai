package com.agustin.ai.rag.document.dto;

import com.agustin.ai.rag.document.Document;
import com.agustin.ai.rag.document.DocumentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponseRecord(
        UUID id,
        String filename,
        String fileHashSha256,
        DocumentStatus status,
        LocalDateTime createdAt
) {
    public static DocumentResponseRecord fromEntity(Document document) {
        return new DocumentResponseRecord(
                document.getId(),
                document.getFilename(),
                document.getFileHashSha256(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }
}