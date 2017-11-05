package com.lovedev.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;

/**
 * lovedev 2017. 11. 5. PM 1:36
 */
@Slf4j
public class HashUtils {

  private static MessageDigest SHA1;

  static {
    try {
      SHA1 = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }


  public static String makeHash(String str){
    if(SHA1 == null){
      log.warn("SHA1 MessageDigest 로딩 실패");
      return null;
    }

    try {
      SHA1.update(str.getBytes("UTF-8"));
      byte[] digest = SHA1.digest();
      return Base64.getEncoder().encodeToString(digest);
    } catch (UnsupportedEncodingException e) {
      log.warn("SHA1 MessageDigest 해쉬실패");
      return null;
    }

  }
}
