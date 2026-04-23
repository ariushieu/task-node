package learning.tasknode.repository;

import learning.tasknode.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    org.springframework.data.domain.Page<Comment> findByTaskIdAndIsDeletedFalseOrderByCreatedAtAsc(Long taskId, org.springframework.data.domain.Pageable pageable);
}
