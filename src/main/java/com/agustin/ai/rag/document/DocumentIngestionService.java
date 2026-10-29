package com.agustin.ai.rag.document;

import com.agustin.ai.rag.document.dto.DocumentResponseRecord;

public interface DocumentIngestionService {
    DocumentResponseRecord registerDocument(String filename, String fileHashSha256);
}
