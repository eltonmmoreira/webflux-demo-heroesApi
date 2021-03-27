package com.demo.heroesApi.controller;

import com.demo.heroesApi.domain.Hero;
import com.demo.heroesApi.exception.NoResultException;
import com.demo.heroesApi.service.HeroService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@RestController
@RequestMapping("/hero")
public class HeroController {

    private HeroService heroService;

    public HeroController(HeroService heroService) {
        this.heroService = heroService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Hero> findAll() {
        return heroService.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "500")
    })
    @PostMapping
    public Mono<ResponseEntity<Hero>> create(@RequestBody @NonNull Hero hero) {
        return heroService.save(hero)
                .map(heroMono -> new ResponseEntity<>(heroMono, HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Hero>> update(@RequestBody @NonNull Hero hero,
                                             @PathVariable @NonNull String id) {
        return heroService.update(hero, id)
                .map(h -> new ResponseEntity<>(h, HttpStatus.OK))
                .onErrorReturn(NoResultException.class, new ResponseEntity<>(HttpStatus.NOT_FOUND));

//        return heroService.findById(id){
//                .flatMap(h -> {
//                    hero.setId(id);
//                    return heroService.save(hero)
//                            .map(hero2 -> new ResponseEntity<>(hero2, HttpStatus.OK));
//                }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Hero>> findById(@PathVariable @NonNull String id) {
        return heroService.findById(id)
                .map(heroMono -> new ResponseEntity<>(heroMono, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "202"),
            @ApiResponse(responseCode = "404")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @NonNull String id) {
        return heroService.findById(id)
                .flatMap(existingHero ->
                        heroService.delete(id)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.ACCEPTED)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
