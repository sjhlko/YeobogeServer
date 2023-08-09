package com.yeoboge.server;

import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.domain.entity.User;
import com.yeoboge.server.repository.GenreRepository;
import com.yeoboge.server.repository.UserRepository;
import com.yeoboge.server.service.BoardGameService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

@SpringBootTest
@Transactional
public class EntityTest {
    @Autowired
    BoardGameService service;
    @Autowired
    UserRepository repository;

    @Test
    public void test() {
        User user = repository.getById(1L);
        service.rateBoardGame(1L, user.getId(), 4.5);
        user.getRatings();
        service.rateBoardGame(1L, user.getId(), 3.0);
        user.getRatings();
    }
}
