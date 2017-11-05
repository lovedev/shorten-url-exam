package com.lovedev.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * lovedev 2017. 11. 4. AM 9:26
 */

@Data
@Entity
@Table(name = "t_shorten_url")
public class ShortenUrl {

  @Id
  @GeneratedValue
  private Long ID;

  @Column(name = "url", length = 255)
  private String url;

  @Column(name = "visit_count", columnDefinition = "int default 0")
  private Long visitCount = 0L;

  @Column(name = "hash")
  private String hash;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private Status status = Status.ACTIVE;

  public enum Status{ ACTIVE, INACTIVE }
}
