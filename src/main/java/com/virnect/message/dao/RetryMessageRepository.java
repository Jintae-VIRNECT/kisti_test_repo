package com.virnect.message.dao;

import com.virnect.message.domain.MessageType;
import com.virnect.message.domain.RetryMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Project: PF-Message
 * DATE: 2021-03-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface RetryMessageRepository extends CrudRepository<RetryMessage, String> {
}
