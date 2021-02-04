package au.com.ibenta.test.service;

import au.com.ibenta.test.model.User;
import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserEntity> save(User user) {
        UserEntity userEntity = new UserEntity(user);
        return Mono.just(userRepository.save(userEntity));
    }

    public Mono<UserEntity> getById(final Long id) {
        return Mono.just(userRepository.getById(id));
    }

    public Mono<UserEntity> update(final Long id, User user) {
        UserEntity userEntity = new UserEntity(user, id);
        return Mono.just(userRepository.save(userEntity));
    }

    public void delete(final Long id) {
        System.out.println("DELETING ID: " + id);
        userRepository.deleteById(id);
    }

    public Flux<List<UserEntity>> findAll() {
        return Flux.just(userRepository.findAll());
    }

}
