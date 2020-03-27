package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public interface ProjectRepository extends MongoRepository<Project, String> {

    Project findByTag(String tag);

    Project findByIdIsNotAndTag(String id, String tag);

    List<Project> findByIdIsIn(List<String> ids);
}
