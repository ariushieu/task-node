package learning.tasknode.repository;

import learning.tasknode.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(learning.tasknode.enums.UserRole role);
}
