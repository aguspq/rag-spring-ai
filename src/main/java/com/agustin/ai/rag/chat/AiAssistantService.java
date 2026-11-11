package com.agustin.ai.rag.chat;

import com.agustin.ai.rag.chat.dto.ChatRequestRecord;
import com.agustin.ai.rag.chat.dto.ChatResponseRecord;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import com.agustin.ai.rag.document.repository.DocumentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiAssistantService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final DocumentRepository documentRepository;

    @Value("${rag.similarity.top-k:4}")
    private int topK;

    @Value("${rag.similarity.threshold:0.6}")
    private double similarityThreshold;

    public AiAssistantService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, DocumentRepository documentRepository) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        this.documentRepository = documentRepository;
    }

    public List<DocumentResponseRecord> getAllDocuments(){
//        List<com.agustin.ai.rag.document.Document> documents = documentRepository.findAll();
//
//        List<DocumentResponseRecord> finalList = new ArrayList<>();
//
//        for(com.agustin.ai.rag.document.Document document : documents){
//            finalList.add(DocumentResponseRecord.fromEntity(document));
//        }
//        return finalList;
//        with stream
        return documentRepository.findAll()
                .stream()
                .map(DocumentResponseRecord::fromEntity)
                .toList();
    }

    public String generateResponse(ChatRequestRecord request) {
        SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
                .query(request.userQuery())
                .topK(topK)
                .similarityThreshold(similarityThreshold);

        if(request.fileName() != null && !request.fileName().isBlank()){
            String filterExpression = String.format("file_name == '%s'", request.fileName());
            searchRequestBuilder.filterExpression(filterExpression);
        }

        List<Document> relevantDocuments = vectorStore.similaritySearch(searchRequestBuilder.build());

        if(relevantDocuments.isEmpty()){
            return "Sorry, I haven't got relevant information in my data base to answer your question";
        }

        String context = relevantDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        String systemPromptTemplate = """
                You are an AI assistant expert in document analysis. Answer the user's question
                relying exclusively on the provided context below. If the answer cannot be found
                in the context, clearly state that you do not have enough information.
                
                Context:
                %s
                """;

        String finalSystemPrompt = String.format(systemPromptTemplate, context);

        return this.chatClient
                .prompt()
                .system(finalSystemPrompt)
                .user(request.userQuery())
                .call() // sends the request to AI model
                .content(); // returns AI's response
    }
}