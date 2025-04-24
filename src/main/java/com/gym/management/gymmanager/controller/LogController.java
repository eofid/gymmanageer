package com.gym.management.gymmanager.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private static final String LOG_DIRECTORY = "logs/";

    @GetMapping("/{date}")
    public ResponseEntity<Resource> getLogFileByDate(@PathVariable String date) {
        try {
            // Форматируем дату
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Имя файла логов (например logs/app.log или logs/app-2025-04-23.log если ты ротацию подключишь)
            String filename = "app.log"; // или "app-" + date + ".log" если хочешь по дням
            File file = new File(LOG_DIRECTORY + filename);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            FileSystemResource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
