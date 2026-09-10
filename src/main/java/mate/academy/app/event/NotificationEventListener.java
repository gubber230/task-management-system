package mate.academy.app.event;

import lombok.RequiredArgsConstructor;
import mate.academy.app.service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleTaskAssignedEvent(TaskAssignedEvent event) {
        String subject = "Task Update: " + event.taskName();
        notificationService.sendNotification(event.assigneeId(), subject, event.message());
    }
}
