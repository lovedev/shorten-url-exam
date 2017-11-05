package com.lovedev.application;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.stream.LongStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ShortenUrlServiceImplTest {

  @Autowired
  ShortenUrlServiceImpl shortenUrlService;

  @Test
  public void createShortenUrl() throws Exception{
    LongStream.range(1, 10000).forEach(ID -> {
      String url = shortenUrlService.convertIDToShortenURL(ID);
      long convertShortenURLToID = shortenUrlService.convertShortenURLToID(url);
      //log.info("{} -> {} = {}",ID, url, convertShortenURLToID);
      assertThat(convertShortenURLToID, is(ID));
    });
  }



}