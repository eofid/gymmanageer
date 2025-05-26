package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.logging.AsyncLogTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private static final String LOG_DIRECTORY = "logs/";
    private final Map<String, AsyncLogTask> taskMap = new ConcurrentHashMap<>();

    private final LogService self;

    public LogService(@Lazy LogService self) {
        this.self = self;
    }

    public String startAsyncLogCreation(String date) {
        String taskId = UUID.randomUUID().toString();
        AsyncLogTask task = new AsyncLogTask();
        task.setStatus(AsyncLogTask.Status.IN_PROGRESS);
        taskMap.put(taskId, task);
        self.createLogFileAsync(taskId, date);
        return taskId;
    }

    @Async
    public void createLogFileAsync(String taskId, String date) {
        AsyncLogTask task = taskMap.get(taskId);

        try {
            Thread.sleep(20000); // Симуляция задержки обработки

            File logDirectory = new File(LOG_DIRECTORY);
            File[] logFiles = logDirectory.listFiles((dir, name) ->
                    name.startsWith("application-") && name.endsWith(".log"));

            if (logFiles == null || logFiles.length == 0) {
                task.setStatus(AsyncLogTask.Status.FAILED);
                logger.warn("No log files found in the directory: {}", logDirectory.getAbsolutePath());
                return;
            }

            File filteredFile = File.createTempFile("filtered-log-", ".log");
            logger.info("Temporary filtered log file created at: {}", filteredFile.getAbsolutePath());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFile))) {
                boolean foundLines = false;

                for (File logFile : logFiles) {
                    logger.info("Processing log file: {}", logFile.getName());

                    try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains(date)) {
                                writer.write(line);
                                writer.newLine();
                                foundLines = true;
                            }
                        }
                    }
                }

                if (!foundLines) {
                    logger.info("No matching lines found for date: {}", date);
                }

                task.setFilePath(filteredFile.getAbsolutePath());
                task.setStatus(AsyncLogTask.Status.COMPLETED);
            }
        } catch (Exception e) {
            logger.error("Exception occurred while creating log file asynchronously", e);
            task.setStatus(AsyncLogTask.Status.FAILED);
        }
    }

    public AsyncLogTask getTaskStatus(String taskId) {
        return taskMap.get(taskId);
    }

    public File getLogFileByTaskId(String taskId) {
        AsyncLogTask task = taskMap.get(taskId);
        if (task == null) {
            return null;
        }
        if (task.getStatus() == AsyncLogTask.Status.COMPLETED) {
            return new File(task.getFilePath());
        }
        return null;
    }
}
