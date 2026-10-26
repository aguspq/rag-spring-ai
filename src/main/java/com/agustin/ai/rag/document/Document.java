package com.agustin.ai.rag.document;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String filename; // file original name

    @Column(name = "file_hash_sha256", unique = true, nullable = false, length = 64)
    private String fileHashSha256;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false) // enum: PENDING, PROCESSED, FAILED
    private DocumentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Document(){
    }

    public Document(String filename, String fileHashSha256, DocumentStatus status){
        this.filename = filename;
        this.fileHashSha256 = fileHashSha256;
        this.status = status;
        createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileHashSha256() {
        return fileHashSha256;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

}
