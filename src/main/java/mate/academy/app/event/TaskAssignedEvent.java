package mate.academy.app.event;

public record TaskAssignedEvent(
        Long assigneeId,
        String taskName,
        String message
) {
}
