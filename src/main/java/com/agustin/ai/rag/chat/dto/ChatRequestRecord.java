package com.agustin.ai.rag.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestRecord(
        @NotBlank
        String userQuery,
        String fileName
) {
}
