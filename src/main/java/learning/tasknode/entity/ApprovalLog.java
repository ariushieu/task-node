package learning.tasknode.entity;

import jakarta.persistence.*;
import learning.tasknode.enums.ApprovalAction;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "approval_logs", indexes = {
        @Index(name = "idx_approval_task", columnList = "task_id"),
        @Index(name = "idx_approval_approver", columnList = "approver_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ApprovalLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private ApprovalAction action;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
}
