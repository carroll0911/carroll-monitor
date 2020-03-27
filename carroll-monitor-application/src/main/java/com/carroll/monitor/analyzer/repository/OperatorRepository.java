package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.Operator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public interface OperatorRepository extends MongoRepository<Operator, String> {

    Operator findByEmail(String email);

    Operator findTopByEmailOrMobile(String email, String mobile);

    Operator findByMobile(String mobile);

    Operator findByIdIsNotAndEmail(String id, String email);

    Operator findByIdIsNotAndMobile(String id, String mobile);

    Page<Operator> advanceQuery(List<String> projectIds, Pageable pageable);

    List<Operator> findAllByIdIsIn(List<String> ids);
}
