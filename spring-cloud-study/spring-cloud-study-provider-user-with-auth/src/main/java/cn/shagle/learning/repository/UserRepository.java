package cn.shagle.learning.repository;

import cn.shagle.learning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Danlu on 2017/5/12.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
