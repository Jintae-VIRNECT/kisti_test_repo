package com.virnect.content.infra.message;

public interface MessageService {

	void convertAndSend(String exchangeName, String routingKey, Object pushRequest);
}
