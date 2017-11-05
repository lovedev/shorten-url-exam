package com.lovedev.application;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortenUrlRepository extends JpaRepository<ShortenUrl, Long> {

  //url hash에 대응되는 단축 URL 찾기
  ShortenUrl findByHash(String hash);

}
