package mate.academy.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    private LocalDate dueDate;
    @Column(nullable = false)
    private Long projectId;
    @Column(nullable = false)
    private Long assigneeId;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tasks_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();
}
