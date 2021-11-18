package com.virnect.content.dao.content;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.YesOrNo;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-15
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Domain Repository Class
 */
public interface ContentRepository extends JpaRepository<Content, Long>, ContentCustomRepository {
	@Transactional(readOnly = true)
	Optional<Content> findByUuid(String contentUUID);

	long countByConverted(YesOrNo yesOrNo);

	long countByShared(YesOrNo yesOrNo);

	long countByDeleted(YesOrNo yesOrNo);

	@Transactional
	Long deleteByUuid(String contentUUID);
}
