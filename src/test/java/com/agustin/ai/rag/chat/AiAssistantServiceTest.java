package com.agustin.ai.rag.chat;

import com.agustin.ai.rag.chat.dto.ChatRequestRecord;
import com.agustin.ai.rag.document.DocumentStatus;
import com.agustin.ai.rag.document.dto.DocumentResponseRecord;
import com.agustin.ai.rag.document.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AiAssistantServiceTest {
    @Mock
    private ChatClient.Builder chatClientBuilder;
    @Mock
    private ChatClient chatClient;
    @Mock
    private VectorStore vectorStore;
    @Mock
    private DocumentRepository documentRepository;
    private AiAssistantService aiAssistantService;


    @BeforeEach
    public void setUp(){
        when(chatClientBuilder.build()).thenReturn(chatClient);
        aiAssistantService = new AiAssistantService(chatClientBuilder, vectorStore, documentRepository);
    }

    @Test
    public void testGenerateResponseWhenNoRelevantDocumentsFound(){
        String query = "Anything";
        ChatRequestRecord chatRequestRecord = new ChatRequestRecord(query, null);
        List<org.springframework.ai.document.Document> emptyList = new ArrayList<>();

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(emptyList);

        String response = aiAssistantService.generateResponse(chatRequestRecord);

        assertEquals("Sorry, I haven't got relevant information in my data base to answer your question", response);
        verifyNoInteractions(chatClient);

    }

    @Test
    public void testGenerateResponseWhenFileNameIsProvided(){
        // ARRANGE
        String query = "What's the pdf about?";
        ChatRequestRecord chatRequestRecord = new ChatRequestRecord(query, "opiManual.pdf");

        // relevant documents
        List<Document> mockDocuments = List.of(
                new Document("Content of test PDF")
        );
        // need to use ArgumentCaptor because, I create the objet INSIDE (in generateResponse())
        ArgumentCaptor<SearchRequest> searchRequestCaptor = ArgumentCaptor.forClass(SearchRequest.class);

        when(vectorStore.similaritySearch(searchRequestCaptor.capture())).thenReturn(mockDocuments);

        // mock the ChatClient call chain
        ChatClient.ChatClientRequestSpec requestSpec = org.mockito.Mockito.mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = org.mockito.Mockito.mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.user(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("This is the AI's response, based on the manual.");

        // ACT
        String response = aiAssistantService.generateResponse(chatRequestRecord);

        // captured the SearchRequest that traveled through the system
        SearchRequest capturedRequest = searchRequestCaptor.getValue();

        // ASSERT

        Filter.Expression expectedExpression = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("file_name"),
                new Filter.Value("opiManual.pdf")
        );
        // we use 'expectedExpression' to compare the same kind of object
        assertEquals(expectedExpression, capturedRequest.getFilterExpression());
        assertEquals("This is the AI's response, based on the manual.", response);

    }

    @Test
    public void testGenerateResponseWhenNoFileNameIsProvided(){
//        ARRANGE
        String query = "Whats the pdf about";
        ChatRequestRecord chatRequestRecord = new ChatRequestRecord(query, null);

        List<Document> relevantDocuments = List.of(
                new Document("Content of test PDF")
        );

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(relevantDocuments);

        // mock the ChatClient call chain
        ChatClient.ChatClientRequestSpec requestSpec = org.mockito.Mockito.mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponseSpec = org.mockito.Mockito.mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.user(any(String.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("This is the AI's response, based on the manual.");

//        ACT
        String response = aiAssistantService.generateResponse(chatRequestRecord);

        assertEquals("This is the AI's response, based on the manual.", response);

    }

    @Test
    public void testGetAllDocumentsWhenListIsNull(){
        List<com.agustin.ai.rag.document.Document> mockList = List.of();

        when(documentRepository.findAll()).thenReturn(mockList);

        List<DocumentResponseRecord> responseList = aiAssistantService.getAllDocuments();

        assertEquals(0, responseList.size());

    }

    @Test
    public void testGetAllDocumentsWhenListIsNotNull(){
        List<com.agustin.ai.rag.document.Document> mockList = List.of(
                new com.agustin.ai.rag.document.Document(
                        "testFile",
                        "67a6f45778858e7d70aa01707a17e8858fb63cda1250f45e8669c9074f791acd",
                        DocumentStatus.PROCESSED
                ),
                new com.agustin.ai.rag.document.Document(
                        "testFile",
                        "67a6f45778858e7d70aa01707a17e8858fb63cda1250f45e8669c9074f791acd",
                        DocumentStatus.PROCESSED
                )
        );

        when(documentRepository.findAll()).thenReturn(mockList);

        List<DocumentResponseRecord> responseList = aiAssistantService.getAllDocuments();


        assertEquals(2, responseList.size());

    }

}