package com.demo.heroesApi.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hero")
public class Hero {
    @Id
    private String id;
    private String nome;
    private String universo;
    private Integer filmes;
}
