package com.virnect.content.dao.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.content.domain.project.Project;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectCustomRepository{
}
