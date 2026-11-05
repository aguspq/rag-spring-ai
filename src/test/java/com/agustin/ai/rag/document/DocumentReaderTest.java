package com.agustin.ai.rag.document;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DocumentReaderTest {
    @Autowired
    private DocumentIngestionServiceImpl documentIngestionService;

    @Test
    public void testIngestDocument() throws NoSuchAlgorithmException, IOException {
        String filePath = "opiManual.pdf";

        DocumentResponseRecord response = documentIngestionService.ingestDocument(filePath);

        assertNotNull(response);
        assertEquals(filePath, response.filename());
        assertEquals(DocumentStatus.PROCESSED, response.status());

    }

}
