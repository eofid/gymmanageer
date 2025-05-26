package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.logging.AsyncLogTask;
import com.gym.management.gymmanager.service.LogService;
import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateLogAsync(@RequestParam String date) {
        String taskId = logService.startAsyncLogCreation(date);
        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<String> getStatus(@PathVariable String taskId) {
        AsyncLogTask task = logService.getTaskStatus(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task.getStatus().name());
    }

    @GetMapping("/download/{taskId}")
    public ResponseEntity<Object> downloadFile(@PathVariable String taskId) {
        AsyncLogTask task = logService.getTaskStatus(taskId);

        if (task == null) {
            return ResponseEntity.status(404).body("Задача с таким ID не найдена.");
        }

        switch (task.getStatus()) {
            case IN_PROGRESS:
                return ResponseEntity.status(404).body("Файл ещё не готов. Попробуйте позже.");
            case FAILED:
                return ResponseEntity.status(500).body("Ошибка при создании файла.");
            case COMPLETED:
                File file = logService.getLogFileByTaskId(taskId);
                if (file == null || !file.exists()) {
                    return ResponseEntity.status(404).body("Файл не найден.");
                }
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                        .body(new FileSystemResource(file));
            default:
                return ResponseEntity.status(500).body("Неизвестный статус задачи.");
        }
    }

}
