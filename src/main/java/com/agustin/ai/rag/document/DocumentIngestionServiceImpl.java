package com.agustin.ai.rag.document;

import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import com.agustin.ai.rag.document.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;


import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentIngestionServiceImpl implements DocumentIngestionService{
    private final DocumentRepository documentRepository;

    public DocumentIngestionServiceImpl(DocumentRepository documentRepository){
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional
    public DocumentResponseRecord registerDocument(String filename, String fileHashSha256){
//        validate
        if(documentRepository.existsByFileHashSha256(fileHashSha256)){
            throw new IllegalArgumentException("Document with hash " + fileHashSha256 + " is already register.");
        }

//        create entity with PENDING status
        Document document = new Document(filename, fileHashSha256, DocumentStatus.PENDING);

        Document savedDocument = documentRepository.save(document);

        return DocumentResponseRecord.fromEntity(savedDocument);

    }

    public String documentReader (String filePath){
        Resource pdfResource = new ClassPathResource(filePath);
        PagePdfDocumentReader r = new PagePdfDocumentReader(pdfResource);

        List<org.springframework.ai.document.Document> list = r.get();

        System.out.println(list);

return "s";
    }

    private byte[] objectToHash(Resource pdfResource) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(pdfResource.getContentAsByteArray());
        System.out.println("TEXT: " + encodedhash);

        System.out.println(bytesToHex(encodedhash));
        return encodedhash;
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

    public DocumentResponseRecord ingestDocument(String filePath) throws NoSuchAlgorithmException, IOException {

        Resource pdfResource = new ClassPathResource(filePath);
        byte[] hashSha = objectToHash(pdfResource);
        String hexHashSha = bytesToHex(hashSha);

        DocumentResponseRecord response = registerDocument(filePath, hexHashSha);

        return response;
    }
}
