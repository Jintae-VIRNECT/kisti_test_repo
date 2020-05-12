package com.virnect.message.dao;

import com.virnect.message.domain.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: PF-Message
 * DATE: 2020-04-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface MailHistoryRepository extends JpaRepository<MailHistory,Long> {
}
