package com.lovedev.application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.lovedev.util.HashUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * lovedev 2017. 11. 3. PM 9:06
 */
@Slf4j
@Service
public class ShortenUrlServiceImpl {

  //--------------------------------------------------------------------------------------------------------------------
  // Variable
  //--------------------------------------------------------------------------------------------------------------------
  @Value("${server.port}")
  private int serverPort;

  @Value("${shorten.url.host}")
  private String shortenUrlHost;

  //--------------------------------------------------------------------------------------------------------------------
  // Constant
  //--------------------------------------------------------------------------------------------------------------------

  private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


  //--------------------------------------------------------------------------------------------------------------------
  // Autowired
  //--------------------------------------------------------------------------------------------------------------------
  private final ShortenUrlRepository shortenUrlRepository;

  public ShortenUrlServiceImpl(ShortenUrlRepository shortenUrlRepository) {
    this.shortenUrlRepository = shortenUrlRepository;
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Public Method
  //--------------------------------------------------------------------------------------------------------------------

  /**
   * 단축URL 생성
   * @param url target url
   * @return shorten url
   */
  public String makeShortenUrl(String url){

    String hash = HashUtils.makeHash(url);
    ShortenUrl existsInfo = getShortenUrlByHash(hash);

    if(existsInfo != null){
      log.info("{}", existsInfo);
      return convertIDToShortenURL(existsInfo.getID());
    }

    ShortenUrl shortenUrl = new ShortenUrl();
    shortenUrl.setUrl(url);
    shortenUrl.setHash(hash);
    shortenUrl = shortenUrlRepository.save(shortenUrl);
    return convertIDToShortenURL(shortenUrl.getID());
  }

  /**
   * 단축ID로 등록된 단축URL 정보 조회
   * @param shortenUrlID
   * @return shortenUrl 정보
   */
  public Optional<ShortenUrl> getShortenUrlByShortenUrlID(String shortenUrlID){
    long convertShortenURLToID = convertShortenURLToID(shortenUrlID);
    if(convertShortenURLToID == 0){
      return Optional.empty();
    }

    ShortenUrl shortenUrl = getShortenUrlByID(convertShortenURLToID);
    if(shortenUrl == null){
      return Optional.empty();
    }

    return Optional.of(shortenUrl);
  }

  /**
   * 방문카운트 update
   * @param shortenUrl
   */
  public void plusVisitCount(ShortenUrl shortenUrl){
    log.info("add count");
    shortenUrl.setVisitCount(shortenUrl.getVisitCount() + 1);
    shortenUrlRepository.save(shortenUrl);
  }

  /**
   * hash값으로 url 정보 조회
   * @param hash url 정보용 hash값
   * @return 조회결과
   */
  public ShortenUrl getShortenUrlByHash(String hash){
    return shortenUrlRepository.findByHash(hash);
  }

  /**
   * hash값으로 url 정보 조회
   * @param ID pk
   * @return 조회결과
   */
  public ShortenUrl getShortenUrlByID(long ID){
    return shortenUrlRepository.findOne(ID);
  }


  //Long ID -> Base62 String
  public String convertIDToShortenURL(long id){
    int baseLength = BASE62.length();
    long depth = id;

    StringBuilder sb = new StringBuilder();
    char[] base62Array = BASE62.toCharArray();
    do {
      int mod = Math.toIntExact(depth % baseLength);
      sb.append(base62Array[mod]);
    } while ((depth = depth / baseLength) != 0);

    String url = String.format("%s:%d/%s", shortenUrlHost, serverPort, sb.toString());;
    if(serverPort == 80) {
      url = String.format("%s/%s", shortenUrlHost, sb.toString());
    }
    return url;
  }

  //Base62 String -> Long ID
  public long convertShortenURLToID(String shortedUrl){
    char[] base62 = shortedUrl.toCharArray();
    long result = 0;
    int depth = 1;

    for(char str : base62){
      int pos = BASE62.indexOf(str);
      result += pos * depth;
      depth *= 62;
    }

    return result;
  }

}
