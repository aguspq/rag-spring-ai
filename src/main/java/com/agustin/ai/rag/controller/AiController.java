package com.agustin.ai.rag.controller;

import com.agustin.ai.rag.service.AiAssistantService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {
    private final AiAssistantService aiAssistantService;

    public AiController(AiAssistantService aiAssistantService){
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/generate")
    public String generate(@RequestBody String message){
        return aiAssistantService.generateResponse(message);
    }
}
