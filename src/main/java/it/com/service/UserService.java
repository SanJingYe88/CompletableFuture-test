package it.com.service;

import it.com.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    public User getById(Integer id) throws Exception {
        log.info("进入 getById, id:{}", id);
        if (id == 1) {
            return new User(1, "冬哥");
        } else if (id == 2) {
            return new User(2, "珣爷");
        } else {
            throw new Exception("未能找到人员");
        }
    }

    public User save(User user) {
        log.info("进入 save, user:{}", user);
        return user;
    }
}

