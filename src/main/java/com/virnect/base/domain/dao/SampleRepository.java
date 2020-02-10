package com.virnect.base.domain.dao;

import com.virnect.base.domain.domain.Sample;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface SampleRepository extends JpaRepository<Sample, Long> {
}
