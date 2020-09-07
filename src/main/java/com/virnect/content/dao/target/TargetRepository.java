package com.virnect.content.dao.target;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.content.domain.Target;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
public interface TargetRepository extends JpaRepository<Target, Long>, TargetCustomRepository {
	Optional<Target> findByData(String targetData);

	Optional<Target> findById(Long id);

	Optional<Target> findByContentId(Long contentId);

	void deleteByContentId(Long contentId);

	int countByData(String targetData);
}
