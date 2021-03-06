package com.sg.twitterstreaming.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.twitterstreaming.controller.TweetDBController;
import com.sg.twitterstreaming.model.DataResponse;
import com.sg.twitterstreaming.model.TweetResponse;
import com.sg.twitterstreaming.model.service.tweet.Data;
import com.sg.twitterstreaming.model.service.tweet.Tweet;
import com.sg.twitterstreaming.repository.TweetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TweetDBController.class)
public class DBControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TweetRepository tweetRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllOfflineTweetsTest() throws Exception {
        DataResponse data=new DataResponse();
        List<TweetResponse> tweetList=new ArrayList<>();
        tweetList.add(new TweetResponse());
        data.setTweetResponse(tweetList);
        String json=objectMapper.writeValueAsString(data);
        when(tweetRepository.findAll()).thenReturn(tweetList);
        MvcResult mvcResult=mockMvc.perform(get("/api/tweets/saved/get-all"))
                .andReturn();
        String expectedResponse=mvcResult.getResponse().getContentAsString();
        assertThat(json).isEqualTo(expectedResponse);
    }


    @Test
    public void addTweetToDBTest() throws Exception {
        TweetResponse test=new TweetResponse();
        ResponseEntity<?> responseEntity= new ResponseEntity<>("Success", HttpStatus.OK);
        when(tweetRepository.save(test)).thenReturn(test);
        MvcResult mvcResult=mockMvc.perform(post("/api/tweets/saved/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)))
                .andReturn();
        String expectedResponse=mvcResult.getResponse().getContentAsString();
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }


    @Test
    public void deleteTweetFromDBTest() throws Exception {
        ResponseEntity<?> responseEntity= new ResponseEntity<>("Success", HttpStatus.OK);
        MvcResult mvcResult=mockMvc.perform(delete("/api/tweets/saved/delete/{id}","1")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String expectedResponse=mvcResult.getResponse().getContentAsString();
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }

}