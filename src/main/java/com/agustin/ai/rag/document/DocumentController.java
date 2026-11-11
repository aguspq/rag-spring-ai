package com.agustin.ai.rag.document;

import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Document Management", description = "Endpoints for document ingestion and processing")
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentController(DocumentIngestionService documentIngestionService){
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ingest a new PDF document",
            description = "Uploads a PDF file, calculates its SHA-256 hash to prevent duplicates, extracts and cleans its text, splits it into chunks, and stores the embedded vectors in the vector database.")
    @ApiResponse(responseCode = "201", description = "Read successfully ")
    @ApiResponse(responseCode = "422", description = "Error processing or reading file")
    @ApiResponse(responseCode = "409", description = "Document already exists")
    @ApiResponse(responseCode = "413", description = "Payload Too Large - File size exceeds the maximum permitted limit (30MB)")

    public ResponseEntity<DocumentResponseRecord> registerDocument(@RequestParam MultipartFile file){
        DocumentResponseRecord response = documentIngestionService.ingestDocument(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

}
