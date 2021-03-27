package com.demo.heroesApi.repository;

import com.demo.heroesApi.domain.Hero;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface HeroRepository extends ReactiveMongoRepository<Hero, String> {

    Flux<Hero> findByUniverso(String universo);

}
