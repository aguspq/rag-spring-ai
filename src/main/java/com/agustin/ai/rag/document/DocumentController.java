package com.agustin.ai.rag.document;

import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;
    private final OllamaChatModel chatModel;

    public DocumentController(DocumentIngestionService documentIngestionService, OllamaChatModel chatModel){
        this.documentIngestionService = documentIngestionService;
        this.chatModel = chatModel;
    }

    @PostMapping
    public ResponseEntity<DocumentResponseRecord> registerDocument(
            @RequestParam String filename,
            @RequestParam String fileHashSha256
    ){
        DocumentResponseRecord response = documentIngestionService.registerDocument(filename, fileHashSha256);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/api/test")
    public String test(@RequestBody String userPrompt){

        return chatModel.call(userPrompt);
    }



}
