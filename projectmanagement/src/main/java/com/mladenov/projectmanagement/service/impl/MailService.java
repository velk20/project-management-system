package com.mladenov.projectmanagement.service.impl;

import com.mladenov.projectmanagement.model.dto.task.PageableTasksDTO;
import com.mladenov.projectmanagement.model.dto.task.TaskDTO;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.repository.UserRepository;
import com.mladenov.projectmanagement.service.ITaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    public String myEmail;

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final ITaskService taskService;

    public MailService(JavaMailSender mailSender, UserRepository userRepository, ITaskService taskService) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }

    public void sendDailyStatusEmails() {
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            String info = gatherInfo(user);
            sendEmail(user.getEmail(), "Daily Info", info);
        }
    }

    private String gatherInfo(UserEntity user) {
        Pageable pageable = PageRequest.of(0, 1000);

        PageableTasksDTO tasksForUser = taskService.getTasksForUser(user.getId(), pageable);
        List<TaskDTO> tasks = tasksForUser.getTasks();

        return buildPendingTasksEmail(tasks);
    }

    private String buildPendingTasksEmail(List<TaskDTO> tasks) {
        List<TaskDTO> pendingTasks = tasks.stream()
                .filter(t -> !"Closed".equalsIgnoreCase(t.getStatus()))
                .toList();

        if (pendingTasks.isEmpty()) {
            return "You have no pending tasks. Great job!";
        }

        StringBuilder message = new StringBuilder();
        message.append("You have ").append(pendingTasks.size()).append(" pending task(s):\n\n");

        for (int i = 0; i < pendingTasks.size(); i++) {
            TaskDTO task = pendingTasks.get(i);
            message.append(i + 1).append(". ")
                    .append("Title: ").append(task.getTitle()).append("\n")
                    .append("   Type: ").append(task.getType()).append("\n")
                    .append("   Status: ").append(task.getStatus()).append("\n\n");
        }

        message.append("Please address them as soon as possible.\n\n")
                .append("Best regards,\nYour Task Management System");

        return message.toString();
    }


    public void sendEmail(String userEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(myEmail);
        message.setTo(userEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
