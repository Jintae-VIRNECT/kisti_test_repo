package com.virnect.content.dao;

import com.virnect.content.domain.SceneGroup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Scene Group Domain Repository Class
 */
public interface SceneGroupRepository extends JpaRepository<SceneGroup, Long> {
}
