package com.agustin.ai.rag.document;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DocumentReaderTest {
    @Autowired
    private DocumentIngestionServiceImpl documentIngestionService;
    @Autowired
    private TextCleaningTransformer textCleaningTransformer;

    @Test
    public void testIngestDocument() throws NoSuchAlgorithmException, IOException {
        String filePath = "opiManual.pdf";

        DocumentResponseRecord respone = documentIngestionService.ingestDocument(filePath);

        assertNotNull(respone);
        assertEquals(filePath, respone.filename());
        assertEquals(DocumentStatus.PROCESSED, respone.status());

    }

//    @Test
//    public void testTextSplitter(){
//        String filePath = "opiManual.pdf";
//
//        List<Document> documents = documentIngestionService.documentReader(filePath);
//
////        List<Document> chunks = documentIngestionService.textSplitter(documents);
//        List<Document> cleanText = textCleaningTransformer.apply(documents);
//        List<Document> cleanDocuments = documentIngestionService.textSplitter(cleanText);
//        System.out.println(cleanDocuments);
//
////        int index = 1;
////        for(Document chunk : chunks){
////            System.out.println("--------------------------------------------------------------------\n");
////            System.out.println("Chunk " + index++ + " :");
////            System.out.println(chunk.getText());
////        }
//
//
//    }

}
