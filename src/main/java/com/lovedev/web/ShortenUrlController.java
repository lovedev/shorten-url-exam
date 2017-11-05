package com.lovedev.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lovedev.application.ShortenUrl;
import com.lovedev.application.ShortenUrl.Status;
import com.lovedev.application.ShortenUrlDto.Create;
import com.lovedev.application.ShortenUrlServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * lovedev 2017. 11. 3. PM 9:06
 */
@Slf4j
@Controller
public class ShortenUrlController {

  //--------------------------------------------------------------------------------------------------------------------
  // Autowired
  //--------------------------------------------------------------------------------------------------------------------
  private final ShortenUrlServiceImpl shortenUrlService;

  private static UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

  @Autowired
  public ShortenUrlController(ShortenUrlServiceImpl shortenUrlService) {
    this.shortenUrlService = shortenUrlService;
  }

  //--------------------------------------------------------------------------------------------------------------------
  // view controller method
  //--------------------------------------------------------------------------------------------------------------------

  //View 컨트롤 테스트
  @RequestMapping(value = "/hello")
  public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
    model.addAttribute("name", name);
    return "hello";
  }

  //URL FORM
  @RequestMapping(value = "/")
  public String shortenUrl(Model model){
    return "shorten-url/index";
  }

  //redirect
  @RequestMapping(value = "/{shortenID}")
  public ResponseEntity forward(@PathVariable(required = true) String shortenID) throws URISyntaxException {
    Optional<ShortenUrl> shortenUrlOpt = shortenUrlService.getShortenUrlByShortenUrlID(shortenID);
    if(!shortenUrlOpt.isPresent()){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    ShortenUrl shortenUrl = shortenUrlOpt.get();
    if(shortenUrl.getStatus() == Status.INACTIVE){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    shortenUrlService.plusVisitCount(shortenUrl);

    String url = shortenUrl.getUrl();
    URI shortenUri = new URI(url);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(shortenUri);

    //100회 이상 방문된 URL이라면
    if(shortenUrl.getVisitCount() > 100){
      return new ResponseEntity<>(httpHeaders, HttpStatus.PERMANENT_REDIRECT);
    }

    return new ResponseEntity<>(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);
  }

  //--------------------------------------------------------------------------------------------------------------------
  // rest api controller method
  //--------------------------------------------------------------------------------------------------------------------

  //URL 생성
  @RequestMapping(method = RequestMethod.POST, value = "/shorten-url/make")
  @ResponseBody public ResponseEntity makeShortenUrl(@Valid @RequestBody Create createInfo){

    if(!isUrl(createInfo.getUrl())){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    String shortenUrl = shortenUrlService.makeShortenUrl(createInfo.getUrl());
    return new ResponseEntity<>(shortenUrl, HttpStatus.OK);
  }


  //--------------------------------------------------------------------------------------------------------------------
  // Private method
  //--------------------------------------------------------------------------------------------------------------------

  private boolean isUrl(String url){
    return urlValidator.isValid(url);
  }
}
