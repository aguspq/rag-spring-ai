package com.agustin.ai.rag.document;

import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentIngestionService {
    DocumentResponseRecord ingestDocument(MultipartFile file);
}
