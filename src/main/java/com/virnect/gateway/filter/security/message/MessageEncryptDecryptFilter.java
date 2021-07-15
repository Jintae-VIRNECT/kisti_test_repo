package com.virnect.gateway.filter.security.message;

import static java.util.function.Function.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.bouncycastle.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyDecoder;
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyEncoder;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import com.virnect.gateway.error.ErrorCode;
import com.virnect.gateway.error.GatewaySecurityException;

@Component
public class MessageEncryptDecryptFilter extends AbstractGatewayFilterFactory<MessageEncryptDecryptFilter.Config> {
	private static final Logger logger = Loggers.getLogger(
		"com.virnect.gateway.filter.security.message.MessageEncryptDecryptFilter");
	private static final String HEADER_ENCRYPT_KEY_NAME = "encrypt";
	private static final String HEADER_DEVICE_AUTH_KEY_NAME = "deviceAuthKey";
	private static final String HEADER_API_NAME = "apiName";
	private static final String SECRET_KEY_NAME = "secretKey";
	private final Map<String, MessageBodyDecoder> messageBodyDecoders;
	private final Map<String, MessageBodyEncoder> messageBodyEncoders;
	private final RedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public MessageEncryptDecryptFilter(
		Set<MessageBodyDecoder> messageBodyDecoders,
		Set<MessageBodyEncoder> messageBodyEncoders, RedisTemplate redisTemplate
	) {
		super(Config.class);
		this.messageBodyDecoders = messageBodyDecoders.stream()
			.collect(Collectors.toMap(MessageBodyDecoder::encodingType, identity()));
		this.messageBodyEncoders = messageBodyEncoders.stream()
			.collect(Collectors.toMap(MessageBodyEncoder::encodingType, identity()));
		this.redisTemplate = redisTemplate;
		this.objectMapper = new ObjectMapper();
	}

	@PostConstruct
	protected void init() {
		logger.info("[MESSAGE ENCRYPT DECRYPT FILTER] => ACTIVE");
	}

	private static String toRaw(Flux<DataBuffer> body) {
		AtomicReference<String> rawReference = new AtomicReference<>();
		body.subscribe(dataBuffer -> {
			byte[] bytes = new byte[dataBuffer.readableByteCount()];
			dataBuffer.read(bytes);
			DataBufferUtils.release(dataBuffer);
			rawReference.set(Strings.fromUTF8ByteArray(bytes));
		});
		return rawReference.get();
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter(((exchange, chain) -> {
			ServerHttpRequest originRequest = exchange.getRequest();
			HttpHeaders httpHeaders = originRequest.getHeaders();

			// if request doesn't have header value related of message encrypt and decrypt then passing request as plain message
			if (!httpHeaders.containsKey(HEADER_ENCRYPT_KEY_NAME) ||
				!httpHeaders.containsKey(HEADER_DEVICE_AUTH_KEY_NAME) ||
				originRequest.getURI().getPath().equals("/auth/app")
			) {
				// move to next filter
				return chain.filter(exchange);
			}

			logger.info("Message Encrypt Decrypt Filter Start");
			String deviceAuthKey = Objects.requireNonNull(httpHeaders.get(HEADER_DEVICE_AUTH_KEY_NAME)).get(0);
			logger.info("[DEVICE_AUTH_KEY] - {}", deviceAuthKey);
			Map<String, String> deviceAuth = redisTemplate.opsForHash().entries("DeviceAuth:" + deviceAuthKey);
			logger.info(deviceAuth.toString());
			String secretKey = deviceAuth.get(SECRET_KEY_NAME);

			if (Objects.equals(originRequest.getMethod(), HttpMethod.GET)
				|| originRequest.getHeaders().getContentLength() == -1) {
				ServerHttpResponse mutateHttpResponse = getServerHttpResponse(exchange, secretKey);
				return chain.filter(exchange.mutate().response(mutateHttpResponse).build());
			} else {
				return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
					ServerHttpRequest mutatedHttpRequest = getServerHttpRequest(exchange, dataBuffer, secretKey);
					ServerHttpResponse mutateHttpResponse = getServerHttpResponse(exchange, secretKey);
					logger.info("Message Encrypt Decrypt Filter End.");
					return chain.filter(
						exchange.mutate().request(mutatedHttpRequest).response(mutateHttpResponse).build());
				});
			}
		}), -2);
	}

	private ServerHttpRequest getServerHttpRequest(
		ServerWebExchange exchange, DataBuffer dataBuffer, String secretKey
	) {
		DataBufferUtils.retain(dataBuffer);
		Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
		String body = toRaw(cachedFlux);
		logger.debug("[REQUEST_ORIGIN_RAW_MESSAGE] - {}", body);
		try {
			EncryptDecryptMessage message = objectMapper.readValue(body, EncryptDecryptMessage.class);
			logger.debug("[ENCRYPTED_MESSAGE] - {}", message.getData());
			String decodeMessage = EncryptDecryptHelper.decrypt(secretKey, message.getData());

			if (decodeMessage.contains("password") || decodeMessage.contains("Password")) {
				logger.debug("[DECRYPTED_MESSAGE] - Skip.. ", decodeMessage);
			}else{
				logger.debug("[DECRYPTED_MESSAGE] - {}", decodeMessage);
			}

			byte[] decryptMessageBytes = decodeMessage.getBytes(StandardCharsets.UTF_8);

			return new ServerHttpRequestDecorator(exchange.getRequest()) {

				@Override
				public HttpHeaders getHeaders() {
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.putAll(exchange.getRequest().getHeaders());
					if (decryptMessageBytes.length > 0) {
						httpHeaders.setContentLength(decryptMessageBytes.length);
					}
					httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
					return httpHeaders;
				}

				@Override
				public Flux<DataBuffer> getBody() {
					return Flux.just(body).map(s -> new DefaultDataBufferFactory().wrap(decryptMessageBytes));
				}
			};
		} catch (JsonProcessingException e) {
			logger.error("EncodingMessage JSON Parsing Error", e);
			throw new GatewaySecurityException(ErrorCode.ERR_MESSAGE_ENCRYPT_DECRYPT);
		}
	}

	private ServerHttpResponse getServerHttpResponse(ServerWebExchange exchange, String secretKey) {
		ServerHttpResponse originResponse = exchange.getResponse();
		ServerHttpResponse mutatedResponse = new ServerHttpResponseDecorator(originResponse) {
			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
				httpHeaders.set(HttpHeaders.CONTENT_ENCODING, "application/octet-stream");
				ClientResponse clientResponse = prepareClientResponse(body, httpHeaders);

				Mono<EncryptDecryptMessage> modifiedBody = extractBody(exchange, clientResponse)
					.flatMap(originResponse -> Mono.just(EncryptDecryptHelper.encrypt(secretKey, originResponse)))
					.flatMap(encodedResponse -> Mono.just(new EncryptDecryptMessage(encodedResponse)));

				BodyInserter<Mono<EncryptDecryptMessage>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(
					modifiedBody, EncryptDecryptMessage.class);

				CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(
					exchange,
					exchange.getResponse().getHeaders()
				);

				return bodyInserter.insert(outputMessage, new BodyInserterContext())
					.then(Mono.defer(() -> {
						Mono<DataBuffer> messageBody = updateBody(getDelegate(), outputMessage);
						HttpHeaders headers = getDelegate().getHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);
						if (headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
							messageBody = messageBody.doOnNext(dataBuffer -> headers.setContentLength(dataBuffer.readableByteCount()));
						}
						return getDelegate().writeWith(messageBody);
					}));
			}

			private Mono<DataBuffer> updateBody(ServerHttpResponse httpResponse, CachedBodyOutputMessage message) {
				Mono<DataBuffer> response = DataBufferUtils.join(message.getBody());
				List<String> encodingHeaders = httpResponse
					.getHeaders()
					.getOrEmpty(HttpHeaders.CONTENT_ENCODING);
				for (String encoding : encodingHeaders) {
					MessageBodyEncoder encoder = messageBodyEncoders.get(encoding);
					if (encoder != null) {
						DataBufferFactory dataBufferFactory = httpResponse.bufferFactory();
						response = response
							.publishOn(Schedulers.parallel())
							.map(encoder::encode).map(dataBufferFactory::wrap);
						break;
					}
				}
				return response;
			}

			private Mono<String> extractBody(ServerWebExchange exchange1, ClientResponse clientResponse) {
				List<String> encodingHeaders = exchange.getResponse().getHeaders()
					.getOrEmpty(HttpHeaders.CONTENT_ENCODING);
				for (String encoding : encodingHeaders) {
					MessageBodyDecoder decoder = messageBodyDecoders.get(encoding);
					if (decoder != null) {
						return clientResponse.bodyToMono(byte[].class)
							.publishOn(Schedulers.parallel())
							.map(bytes -> exchange.getResponse()
								.bufferFactory().wrap(bytes))
							.map(dataBuffer -> prepareClientResponse(
								Mono.just(dataBuffer),
								exchange.getResponse().getHeaders()
							))
							.flatMap(response -> response.bodyToMono(String.class));
					}
				}
				return clientResponse.bodyToMono(String.class);
			}

			private ClientResponse prepareClientResponse(
				Publisher<? extends DataBuffer> body, HttpHeaders httpHeaders
			) {
				ClientResponse.Builder builder = ClientResponse.create(
					Objects.requireNonNull(exchange.getResponse().getStatusCode()),
					HandlerStrategies.withDefaults().messageReaders()
				);
				return builder.headers(headers -> headers.putAll(httpHeaders)).body(Flux.from(body)).build();
			}
		};

		// Additional Header value
		mutatedResponse.getHeaders().set(HEADER_ENCRYPT_KEY_NAME, "true");
		mutatedResponse.getHeaders().set(HEADER_API_NAME, exchange.getRequest().getURI().getRawPath());
		return mutatedResponse;
	}

	public static class Config {
		public Config() {
		}
	}
}