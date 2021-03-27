package com.demo.heroesApi.controller;

import com.demo.heroesApi.domain.Hero;
import com.demo.heroesApi.service.HeroService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@SpringBootTest
public class HeroControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private HeroService heroService;

    @Test
    public void getOneHeroById(){
        final var hero = Hero.builder()
                .nome("Thor")
                .filmes(4)
                .universo("MARVEL")
                .build();

        heroService.save(hero).subscribe(
                savedHero -> {
                    System.out.println(savedHero.getId());

                    webTestClient.get().uri("/hero/{id}", savedHero.getId())
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody();

                    heroService.delete(savedHero.getId()).block();
                }
        );
    }

    @Test
    public void getOneHeroNotFound() {
        webTestClient.get().uri("/hero/{id}" ,"10")
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    public void deleteHero() {
        webTestClient.delete().uri("/hero/{id}", "1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }
}
