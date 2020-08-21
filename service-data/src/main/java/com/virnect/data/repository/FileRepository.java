package com.virnect.data.repository;

import com.virnect.data.dao.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
