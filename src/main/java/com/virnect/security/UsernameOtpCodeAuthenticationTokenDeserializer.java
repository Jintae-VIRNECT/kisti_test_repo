package com.virnect.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

public class UsernameOtpCodeAuthenticationTokenDeserializer
	extends JsonDeserializer<UsernameOtpCodeAuthenticationToken> {
	@Override
	public UsernameOtpCodeAuthenticationToken deserialize(
		JsonParser jp, DeserializationContext ctxt
	) throws IOException, JsonProcessingException {
		UsernameOtpCodeAuthenticationToken token;

		ObjectMapper mapper = (ObjectMapper)jp.getCodec();
		JsonNode jsonNode = mapper.readTree(jp);
		Boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
		JsonNode principalNode = readJsonNode(jsonNode, "principal");
		Object principal = null;
		if (principalNode.isObject()) {
			principal = mapper.readValue(principalNode.traverse(mapper), Object.class);
		} else {
			principal = principalNode.asText();
		}
		JsonNode credentialsNode = readJsonNode(jsonNode, "credentials");
		Object credentials;
		if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
			credentials = null;
		} else {
			credentials = credentialsNode.asText();
		}
		List<GrantedAuthority> authorities = mapper.readValue(
			readJsonNode(jsonNode, "authorities").traverse(mapper), new TypeReference<List<GrantedAuthority>>() {
			});
		if (authenticated) {
			token = new UsernameOtpCodeAuthenticationToken(principal, credentials, authorities);
		} else {
			token = new UsernameOtpCodeAuthenticationToken(principal, credentials);
		}
		JsonNode detailsNode = readJsonNode(jsonNode, "details");
		if (detailsNode.isNull() || detailsNode.isMissingNode()) {
			token.setDetails(null);
		} else {
			Object details = mapper.readValue(detailsNode.toString(), new TypeReference<Object>() {
			});
			token.setDetails(details);
		}
		return token;

	}

	private JsonNode readJsonNode(JsonNode jsonNode, String field) {
		return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
	}
}
