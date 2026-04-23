package learning.tasknode.repository;

import learning.tasknode.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.isDeleted = false")
    java.util.List<ProjectMember> findByProjectIdAndIsDeletedFalse(Long projectId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.isDeleted = false")
    org.springframework.data.domain.Page<ProjectMember> findByProjectIdAndIsDeletedFalse(Long projectId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.user.id = :userId AND pm.isDeleted = false")
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    Optional<ProjectMember> findByProjectIdAndUserIdIncludeDeleted(Long projectId, Long userId);
}