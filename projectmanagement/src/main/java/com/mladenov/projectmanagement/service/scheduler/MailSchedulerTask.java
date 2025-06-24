package com.mladenov.projectmanagement.service.scheduler;

import com.mladenov.projectmanagement.service.impl.MailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailSchedulerTask {
    private final MailService mailService;

    public MailSchedulerTask(MailService mailService) {
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void dailyMailTask() {
        this.mailService.sendDailyStatusEmails();
    }
}
