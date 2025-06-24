package com.mladenov.projectmanagement.config;

import com.mladenov.projectmanagement.service.impl.MailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerConfig {
    private final MailService mailService;

    public KafkaListenerConfig(MailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = "${kafka.topic.project.invitation}", groupId = "default-group")
    public void handleProjectInvitation(String message) {
        String[] parts = message.split(":");
        String email = parts[0];
        String projectName = parts[1];

        this.mailService.sendEmail(email, "Project Invitation", "You have been successfully added to project with name:" + projectName);
    }

    @KafkaListener(topics = "${kafka.topic.task.assignee}", groupId = "default-group")
    public void handleTaskAssignee(String message) {
        String[] parts = message.split(":");
        String email = parts[0];
        String taskTitle = parts[1];

        this.mailService.sendEmail(email, "Task Assigned", "You have been added as assignee for task with title:" + taskTitle);
    }
}
