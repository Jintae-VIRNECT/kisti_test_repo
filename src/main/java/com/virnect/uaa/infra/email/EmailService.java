package com.virnect.uaa.infra.email;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Email Service Interface
 * @since 2020.03.25
 */
public interface EmailService {
	void sendEmail(EmailMessage emailMessage);
}
