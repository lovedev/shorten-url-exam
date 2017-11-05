package com.lovedev.application;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;

/**
 * lovedev 2017. 11. 3. PM 9:11
 */
public class ShortenUrlDto {

  @Getter
  public static class Create{

    @NotBlank
    private String url;
  }

  @Builder
  public static class Result{
    private boolean status;
    private String shortenUrl;
  }

}
