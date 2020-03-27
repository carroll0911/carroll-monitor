package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.enums.Role;
import com.carroll.monitor.analyzer.model.UserProject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public interface UserProjectRepository extends MongoRepository<UserProject, String> {

    Long deleteAllByProjectId(String projectId);

    List<UserProject> findAllByUserId(String userId);

    List<UserProject> findAllByProjectId(String projectId);

    List<UserProject> findAllByProjectIdAndRole(String projectId, Role role);

    List<UserProject> findAllByProjectIdIsIn(List<String> projectId);

}
