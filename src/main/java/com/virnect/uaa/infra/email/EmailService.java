package com.virnect.uaa.infra.email;

import com.virnect.uaa.infra.email.context.MailMessageContext;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Email Service Interface
 * @since 2020.03.25
 */
public interface EmailService {
	void send(MailMessageContext mailMessageContext);

	default String getSystemEmail() {
		return "no-reply@virnect.com";
	}
}
