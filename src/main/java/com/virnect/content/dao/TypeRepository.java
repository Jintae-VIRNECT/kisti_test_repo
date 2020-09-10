package com.virnect.content.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.content.domain.Type;
import com.virnect.content.domain.Types;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
public interface TypeRepository extends JpaRepository<Type, Long> {
	Optional<Type> findByType(Types type);
}
