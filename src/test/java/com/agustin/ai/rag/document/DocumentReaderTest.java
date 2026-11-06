package com.agustin.ai.rag.document;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import com.agustin.ai.rag.exception.DocumentAlreadyExistsException;
import com.agustin.ai.rag.exception.DocumentReadException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // if we don't put --> error: filePath already exists | with it the code makes ROLLBACK
public class DocumentReaderTest {

    private final DocumentIngestionServiceImpl documentIngestionService;

    @Autowired
    public DocumentReaderTest(DocumentIngestionServiceImpl documentIngestionService){
        this.documentIngestionService = documentIngestionService;

    }

    @Test
    public void ingestDocument_ShouldFail_When_DocumentAlreadyExists(){
        String filePath = "opiManual.pdf";
        // 1rst ingestion
        documentIngestionService.ingestDocument(filePath);

        assertThrows(DocumentAlreadyExistsException.class, ()
                -> documentIngestionService.ingestDocument(filePath));

    }

    @Test
    void ingestDocument_ShouldFail_When_DocumentNotFound(){
        String invalidFilePath = "nonvalid.pdf";

        assertThrows(DocumentReadException.class, () -> {
            documentIngestionService.ingestDocument(invalidFilePath);
        });

    }

    @Test
    void ingestDocument_ShouldSuccess_When_ValidDocument(){
        String filePath = "opiManual.pdf";

        DocumentResponseRecord response = documentIngestionService.ingestDocument(filePath);

        assertNotNull(response);
        assertEquals(filePath, response.filename());
        assertEquals(DocumentStatus.PROCESSED, response.status());
    }


}
