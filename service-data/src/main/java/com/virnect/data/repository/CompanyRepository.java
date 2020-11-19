package com.virnect.data.repository;

import com.virnect.data.dao.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByWorkspaceId(final String workspaceId);
}
