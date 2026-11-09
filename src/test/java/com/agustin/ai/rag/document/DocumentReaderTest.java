package com.agustin.ai.rag.document;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import com.agustin.ai.rag.exception.DocumentAlreadyExistsException;
import com.agustin.ai.rag.exception.DocumentReadException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // if we don't put --> error: filePath already exists | with it the code makes ROLLBACK
public class DocumentReaderTest {

    private final DocumentIngestionServiceImpl documentIngestionService;

    Resource resource = new ClassPathResource("opiManual.pdf");
    MultipartFile multipartFile = new MockMultipartFile(
            "file",     // name in @RequestParam (in controller)
            "opiManual.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            resource.getContentAsByteArray()
    );

    @Autowired
    public DocumentReaderTest(DocumentIngestionServiceImpl documentIngestionService) throws IOException {
        this.documentIngestionService = documentIngestionService;

    }

    @Test
    public void ingestDocument_ShouldFail_When_DocumentAlreadyExists(){
        String filePath = "opiManual.pdf";
        // 1rst ingestion
        documentIngestionService.ingestDocument(multipartFile);

        assertThrows(DocumentAlreadyExistsException.class, ()
                -> documentIngestionService.ingestDocument(multipartFile));

    }

    @Test
    void ingestDocument_ShouldFail_When_DocumentNotFound(){
        String invalidFilePath = "nonvalid.pdf";

        assertThrows(DocumentReadException.class, () -> {
            documentIngestionService.ingestDocument(multipartFile);
        });

    }

    @Test
    void ingestDocument_ShouldSuccess_When_ValidDocument(){
        String filePath = "opiManual.pdf";

        DocumentResponseRecord response = documentIngestionService.ingestDocument(multipartFile);

        assertNotNull(response);
        assertEquals(filePath, response.filename());
        assertEquals(DocumentStatus.PROCESSED, response.status());
    }


}
