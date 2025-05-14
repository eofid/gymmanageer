package com.gym.management.gymmanager.service;

import com.gym.management.gymmanager.logging.AsyncLogTask;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogService {
    private static final String LOG_DIRECTORY = "logs/";
    private final Map<String, AsyncLogTask> taskMap = new ConcurrentHashMap<>();

    public String startAsyncLogCreation(String date) {
        String taskId = UUID.randomUUID().toString();
        AsyncLogTask task = new AsyncLogTask();
        taskMap.put(taskId, task);
        createLogFileAsync(taskId, date);
        return taskId;
    }

    public void createLogFileAsync(String taskId, String date) {
        AsyncLogTask task = taskMap.get(taskId);
        try {
            // Получаем все файлы, соответствующие паттерну application-*.log
            File logDirectory = new File(LOG_DIRECTORY);
            File[] logFiles = logDirectory.listFiles((dir, name) -> name.startsWith("application-") && name.endsWith(".log"));

            if (logFiles == null || logFiles.length == 0) {
                task.setStatus(AsyncLogTask.Status.FAILED);
                System.out.println("No log files found in the directory: " + logDirectory.getAbsolutePath());
                return;
            }

            File filteredFile = File.createTempFile("filtered-log-", ".log");
            System.out.println("Temporary filtered log file created at: " + filteredFile.getAbsolutePath());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFile))) {
                boolean foundLines = false;

                for (File logFile : logFiles) {
                    System.out.println("Processing log file: " + logFile.getName());

                    try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Ищем в строках дату
                            if (line.contains(date)) {
                                writer.write(line);
                                writer.newLine();
                                foundLines = true;
                            }
                        }
                    }
                }

                if (!foundLines) {
                    System.out.println("No matching lines found for date: " + date);
                }

                task.setFilePath(filteredFile.getAbsolutePath());
                task.setStatus(AsyncLogTask.Status.COMPLETED);

            }
        } catch (Exception e) {
            e.printStackTrace();
            task.setStatus(AsyncLogTask.Status.FAILED);
        }
    }




    public AsyncLogTask getTaskStatus(String taskId) {
        return taskMap.get(taskId);
    }

    public File getLogFileByTaskId(String taskId) {
        AsyncLogTask task = taskMap.get(taskId);
        if (task != null && task.getStatus() == AsyncLogTask.Status.COMPLETED) {
            return new File(task.getFilePath());
        }
        return null;
    }
}
