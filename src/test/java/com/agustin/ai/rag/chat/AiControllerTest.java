package com.agustin.ai.rag.chat;

import com.agustin.ai.rag.chat.dto.ChatRequestRecord;
import com.agustin.ai.rag.document.DocumentStatus;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
public class AiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AiAssistantService aiAssistantService;

    @Test
    public void testGetAllDocumentsEndpointReturnsOk() throws Exception {
        // ARRANGE
        List<DocumentResponseRecord> mockDocs = List.of(
                new DocumentResponseRecord(
                        UUID.fromString("7f3e49cb-ca18-4a5e-b0af-b7b15683cb92"),
                        "opiManual.pdf",
                        "67a6f45778858e7d70aa01707a17e8858fb63cda1250f45e8669c9074f791acd",
                        DocumentStatus.PROCESSED,
                        LocalDateTime.now())
        );
        when(aiAssistantService.getAllDocuments()).thenReturn(mockDocs);

        // ACT & ASSERT: Lanzamos la petición GET simulada y validamos el JSON de respuesta
        mockMvc.perform(get("/api/v1/chat/docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filename").value("opiManual.pdf"))
                .andExpect(jsonPath("$[0].status").value("PROCESSED"));
    }

    @Test
    public void testGenerateEndpointReturnsOk() throws Exception {
        // ARRANGE
        ChatRequestRecord requestRecord = new ChatRequestRecord("What is this manual about?", null);
        String mockAiResponse = "This manual is about operational procedures.";

        when(aiAssistantService.generateResponse(any(ChatRequestRecord.class))).thenReturn(mockAiResponse);

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/chat/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRecord)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(mockAiResponse));
    }

    @Test
    public void testGenerateEndpointReturnsError400() throws Exception {
        ChatRequestRecord requestRecord = new ChatRequestRecord(null, null);

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/chat/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRecord)))
                .andExpect(status().isBadRequest());
    }


}
