package com.example.react.services;

import com.example.react.domain.Backlog;
import com.example.react.domain.Project;
import com.example.react.domain.ProjectTask;
import com.example.react.exceptions.ProjectNotFoundException;
import com.example.react.repositories.BacklogRepository;
import com.example.react.repositories.ProjectRepository;
import com.example.react.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        try {
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
            projectTask.setBacklog(backlog);
            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);
            projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);
            if (projectTask.getPriority() == 0 || projectTask.getPriority() == null) {
                projectTask.setPriority(3);
            }
            if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TO-DO");
            }
            return projectTaskRepository.save(projectTask);
        } catch (Exception e) {
            throw new ProjectNotFoundException("Project not found");
        }
    }

    public Iterable<ProjectTask> findBacklogById(String id) {
        Project project = projectRepository.findByProjectIdentifier(id);
        if (project == null) {
            throw new ProjectNotFoundException("Project with id " + id +" does not exist");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTbyProjectSequence(String backlogId,String ptId) {
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlogId);
        if (backlog == null) {
            throw new ProjectNotFoundException("Project with id " + backlogId +" does not exist");
        }
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(ptId);
        if ( projectTask == null) {
            throw new ProjectNotFoundException("Project task" + ptId + "not found");
        }
        if (!projectTask.getProjectIdentifier().equals(backlogId)) {
            throw new ProjectNotFoundException("Project Task" + ptId + "does not exist in project: " + backlogId);
        }
        return projectTask;
    }

    public ProjectTask updateProjectSequence(ProjectTask updatedTask, String backlogId, String ptId) {
        ProjectTask projectTask = findPTbyProjectSequence(backlogId,ptId);
        projectTask = updatedTask;
        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlogId, String ptId) {
        ProjectTask projectTask = findPTbyProjectSequence(backlogId, ptId);
        projectTaskRepository.delete(projectTask);
    }
}