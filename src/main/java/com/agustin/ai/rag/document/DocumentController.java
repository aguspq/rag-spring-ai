package com.agustin.ai.rag.document;

import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentController(DocumentIngestionService documentIngestionService){
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping
    public ResponseEntity<DocumentResponseRecord> registerDocument(
            @RequestParam MultipartFile file
    )  {
        DocumentResponseRecord response = documentIngestionService.ingestDocument(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

}
