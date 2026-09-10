package mate.academy.app.service;

public interface NotificationService {
    void sendNotification(Long userId, String subject, String message);
}
