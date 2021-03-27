package com.demo.heroesApi.router;

import com.demo.heroesApi.domain.Hero;
import com.demo.heroesApi.exception.NoResultException;
import com.demo.heroesApi.service.HeroService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class Routes {

    @Bean
    RouterFunction<ServerResponse> heroes(HeroService heroService) {
        var handler = new EventHandler(heroService);
        return route(GET("/hero-router"), handler::findAll)
                .andRoute(POST("/hero-router"), handler::create)
                .andRoute(GET("/hero-router/{id}"), handler::findById)
                .andRoute(PUT("/hero-router/{id}"), handler::update)
                .andRoute(DELETE("/hero-router/{id}"), handler::delete);
    }

    static class EventHandler {

        private final HeroService heroService;

        public EventHandler(HeroService heroService) {
            this.heroService = heroService;
        }

        Mono<ServerResponse> findAll(ServerRequest request) {
            return ok().body(fromPublisher(heroService.findAll(), Hero.class));
        }

        public Mono<ServerResponse> create(ServerRequest request) {
            return request.bodyToMono(Hero.class)
                    .flatMap(h -> heroService.save(h)
                    .flatMap(a -> ServerResponse.status(HttpStatus.CREATED)
                            .body(fromValue(a))));
        }

        public Mono<ServerResponse> update(ServerRequest request) {
            var id = request.pathVariable("id");
            return request.bodyToMono(Hero.class)
                    .flatMap(hero -> heroService.update(hero, id))
                    .flatMap(hero -> ok().body(fromValue(hero)))
                    .onErrorResume(NoResultException.class,
                            e -> ServerResponse.notFound().build());
        }

        public Mono<ServerResponse> findById(ServerRequest request) {
            var id = request.pathVariable("id");
            return heroService.findById(id)
                    .flatMap(hero -> ok()
                            .body(fromValue(hero)));
        }

        public Mono<ServerResponse> delete(ServerRequest request) {
            var id = request.pathVariable("id");
            return heroService.findById(id)
                    .flatMap(h ->
                            heroService.delete(id)
                                    .then(ok().build()))
                    .switchIfEmpty(notFound().build());
        }
    }

}
