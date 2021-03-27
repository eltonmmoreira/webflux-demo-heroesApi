package com.demo.heroesApi.service;

import com.demo.heroesApi.domain.Hero;
import com.demo.heroesApi.exception.NoResultException;
import com.demo.heroesApi.repository.HeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HeroServiceImpl implements HeroService {

    private final HeroRepository repository;

    @Autowired
    public HeroServiceImpl(HeroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<Hero> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Hero> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Hero> save(Hero hero) {
        return repository.save(hero);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Hero> update(Hero hero, String id) {
        return findById(id)
                .flatMap(h -> {
                    hero.setId(id);
                    return save(hero).map(h2 -> h2);
                })
                .switchIfEmpty(Mono.error(new NoResultException(String.format("hero %s not found", id))));
    }
}
