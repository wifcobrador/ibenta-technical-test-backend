package au.com.ibenta.test.service;

import au.com.ibenta.test.model.User;
import au.com.ibenta.test.persistence.UserEntity;
import au.com.ibenta.test.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity save(User user) {
        UserEntity userEntity = new UserEntity(user);
        return userRepository.save(userEntity);
    }

    public UserEntity getById(final Long id) {
        return userRepository.getById(id);
    }

    public UserEntity update(final Long id, User user) {
        UserEntity userEntity = new UserEntity(user, id);
        return userRepository.save(userEntity);
    }

    public void delete(final Long id) {
        System.out.println("DELETING ID: " + id);
        userRepository.deleteById(id);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

}
