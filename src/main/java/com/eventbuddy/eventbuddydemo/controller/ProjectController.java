package com.eventbuddy.eventbuddydemo.controller;

import com.eventbuddy.eventbuddydemo.dto.CreateProjectDto;
import com.eventbuddy.eventbuddydemo.dto.EditProjectDto;
import com.eventbuddy.eventbuddydemo.dto.ProjectDto;
import com.eventbuddy.eventbuddydemo.dto.ProjectFilterDto;
import com.eventbuddy.eventbuddydemo.model.Project;
import com.eventbuddy.eventbuddydemo.model.User;
import com.eventbuddy.eventbuddydemo.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        User currentUser = getCurrentUser();
        log.info("GET /projects - getting all projects for user: {}", currentUser.getEmail());
        List<ProjectDto> projects = projectService.getAllProjects(currentUser);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectDto>> searchProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Project.ProjectStatus status,
            @RequestParam(required = false) LocalDateTime deadlineFrom,
            @RequestParam(required = false) LocalDateTime deadlineTo,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection
    ) {
        User currentUser = getCurrentUser();
        log.info("GET /projects/search - searching projects for user: {} with search={}, status={}", 
                currentUser.getEmail(), search, status);
        
        ProjectFilterDto filter = new ProjectFilterDto();
        filter.setSearch(search);
        filter.setStatus(status);
        filter.setDeadlineFrom(deadlineFrom);
        filter.setDeadlineTo(deadlineTo);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);
        
        List<ProjectDto> projects = projectService.searchProjects(currentUser, filter);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID id) {
        User currentUser = getCurrentUser();
        log.info("GET /projects/{} - getting project for user: {}", id, currentUser.getEmail());
        ProjectDto project = projectService.getProject(id, currentUser);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody CreateProjectDto dto) {
        User currentUser = getCurrentUser();
        log.info("POST /projects - creating project for user: {}", currentUser.getEmail());
        ProjectDto project = projectService.createProject(dto, currentUser);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody EditProjectDto dto
    ) {
        User currentUser = getCurrentUser();
        log.info("PUT /projects/{} - updating project for user: {}", id, currentUser.getEmail());
        ProjectDto project = projectService.updateProject(id, dto, currentUser);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProject(@PathVariable UUID id) {
        User currentUser = getCurrentUser();
        log.info("DELETE /projects/{} - deleting project for user: {}", id, currentUser.getEmail());
        projectService.deleteProject(id, currentUser);
        return ResponseEntity.ok(Map.of("message", "Проект успешно удален"));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}

