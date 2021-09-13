package com.example.react.repositories;

import com.example.react.domain.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
// второй параметр (long) - это project_id
public interface ProjectRepository extends CrudRepository <Project, Long> {

    Project findByProjectIdentifier(String projectid);
    Iterable<Project> findAll();
}
