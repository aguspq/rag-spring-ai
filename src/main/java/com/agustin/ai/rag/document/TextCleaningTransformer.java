package com.agustin.ai.rag.document;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextCleaningTransformer implements DocumentTransformer {
    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> cleanTextList = new ArrayList<>();

        for (Document document : documents) {
            if (document.getText() == null) {
                continue;
            }
            String cleanText = document.getText().replaceAll("\\s+", " ").trim();
            cleanText = cleanText.replace("© 2026 Franco Pisso. Todos los derechos reservados", "").trim();
            Document cleanDocument = new Document(cleanText, document.getMetadata());
            cleanTextList.add(cleanDocument);
        }

        return cleanTextList;
    }
}
