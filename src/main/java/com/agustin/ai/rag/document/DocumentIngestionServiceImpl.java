package com.agustin.ai.rag.document;

import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import com.agustin.ai.rag.document.repository.DocumentRepository;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class DocumentIngestionServiceImpl implements DocumentIngestionService{
    private final DocumentRepository documentRepository;
    private final TokenTextSplitter tokenTextSplitter;
    private final TextCleaningTransformer textCleaningTransformer;

    public DocumentIngestionServiceImpl(DocumentRepository documentRepository, TokenTextSplitter tokenTextSplitter, TextCleaningTransformer textCleaningTransformer){
        this.documentRepository = documentRepository;
        this.tokenTextSplitter = tokenTextSplitter;
        this.textCleaningTransformer = textCleaningTransformer;
    }

    private DocumentResponseRecord registerDocument(String filePath, String fileHashSha256){
//        validate
        if(documentRepository.existsByFileHashSha256(fileHashSha256)){
            throw new IllegalArgumentException("Document with hash " + fileHashSha256 + " is already register.");
        }

//        create entity with PENDING status
        Document document = new Document(filePath, fileHashSha256, DocumentStatus.PENDING);

        Document savedDocument = documentRepository.save(document);

        return DocumentResponseRecord.fromEntity(savedDocument);

    }

    private List<org.springframework.ai.document.Document> documentReader (String filePath){
        Resource pdfResource = new ClassPathResource(filePath);
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource);

        return reader.get();
    }

    private byte[] objectToHash(Resource pdfResource) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        return digest.digest(pdfResource.getContentAsByteArray());
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public DocumentResponseRecord ingestDocument(String filePath) { // change try catch for personalized Exceptions (create it)
        try {
            Resource pdfResource = new ClassPathResource(filePath);
            byte[] hashSha = objectToHash(pdfResource);
            String hexHashSha = bytesToHex(hashSha);

            DocumentResponseRecord response = registerDocument(filePath, hexHashSha);

            List<org.springframework.ai.document.Document> documents = documentReader(filePath);

            List<org.springframework.ai.document.Document> cleanDocuments =  textCleaningTransformer.apply(documents);

            // save them into database
            List<org.springframework.ai.document.Document> chunks = textSplitter(cleanDocuments);

            return response;

        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private List<org.springframework.ai.document.Document> textSplitter(List<org.springframework.ai.document.Document> pdf){
        List<org.springframework.ai.document.Document> chunks = tokenTextSplitter.apply(pdf);
        return chunks;
    }
}
