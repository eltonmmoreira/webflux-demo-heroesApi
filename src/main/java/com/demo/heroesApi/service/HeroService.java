package com.demo.heroesApi.service;

import com.demo.heroesApi.domain.Hero;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HeroService {

    Flux<Hero> findAll();

    Mono<Hero> findById(String id);

    Mono<Hero> save(Hero hero);

    Mono<Void> delete(String id);

    Mono<Hero> update(Hero hero, String id);

}
