package com.gym.management.gymmanager.logging;

import java.util.concurrent.atomic.AtomicReference;

public class AsyncLogTask {
    public enum Status { PENDING, COMPLETED, FAILED, IN_PROGRESS }

    private final AtomicReference<Status> status = new AtomicReference<>(Status.PENDING);
    private String filePath;

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status newStatus) {
        status.set(newStatus);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
