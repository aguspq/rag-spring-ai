package com.agustin.ai.rag.chat;

import com.agustin.ai.rag.chat.dto.ChatRequestRecord;
import com.agustin.ai.rag.chat.dto.ChatResponseRecord;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "AI Chat", description = "Endpoints for interacting with the RAG assistant")
public class AiController {
    private final AiAssistantService aiAssistantService;

    public AiController(AiAssistantService aiAssistantService){
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/generate")
    public ChatResponseRecord generate(@RequestBody @Valid ChatRequestRecord requestRecord){
        String response = aiAssistantService.generateResponse(requestRecord);
        return new ChatResponseRecord(response);
    }

    @GetMapping("/docs")
    public ResponseEntity<List<DocumentResponseRecord>> getAllDocuments(){
        return ResponseEntity.ok(aiAssistantService.getAllDocuments());
    }
}
