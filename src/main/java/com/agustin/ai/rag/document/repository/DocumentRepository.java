package com.agustin.ai.rag.document.repository;

import com.agustin.ai.rag.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    boolean existsByFileHashSha256(String fileHash);
    Optional<Document> findByFileHashSha256(String fileHashSha256);

}
