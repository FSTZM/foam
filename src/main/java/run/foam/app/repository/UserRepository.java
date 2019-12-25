package run.foam.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import run.foam.app.model.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "from User where username = ?1")
    User findUser(String username);
}
