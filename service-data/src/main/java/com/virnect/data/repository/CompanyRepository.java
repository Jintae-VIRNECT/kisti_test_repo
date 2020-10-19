package com.virnect.data.repository;

import com.virnect.data.dao.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByWorkspaceId(final String workspaceId);
}
