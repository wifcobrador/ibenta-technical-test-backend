package au.com.ibenta.test.service;

import au.com.ibenta.test.model.User;
import au.com.ibenta.test.persistence.UserEntity;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Api(tags = "user")
@RestController
@Profile("template")
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    ResponseEntity<Mono<UserEntity>> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    ResponseEntity<Mono<UserEntity>> getUser(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<Mono<UserEntity>> updateUser(@PathVariable("id") final Long id, @RequestBody User user) {
        return new ResponseEntity<>(userService.update(id, user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Mono<UserEntity>> deleteUser(@PathVariable("id") final Long id) {
        userService.delete(id);
        return new ResponseEntity<>(Mono.empty(), HttpStatus.CREATED);
    }

    @GetMapping("")
    ResponseEntity<Flux<List<UserEntity>>> list() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }
}
