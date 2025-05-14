package com.gym.management.gymmanager.controller;

import com.gym.management.gymmanager.logging.AsyncLogTask;
import com.gym.management.gymmanager.service.LogService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

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
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String taskId) {
        File file = logService.getLogFileByTaskId(taskId);

        if (file == null || !file.exists()) {
            return ResponseEntity.notFound().build();
        }

        System.out.println("Returning file for download: " + file.getAbsolutePath());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
    }


}
