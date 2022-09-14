package com.example;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

@SpringBootTest
class HttpclientHangDemoApplicationTests {

	@Test
	@Timeout(value = 5)
	void testWebClient() {
		WebClient client = WebClient.builder().apply(customize()).build();
		client.get()
				.uri("path")
				.retrieve()
				.bodyToMono(Void.class)
				.block();
	}

	private Consumer<WebClient.Builder> customize() {
		return builder -> {
			ClientHttpConnector connector = buildClientConnector();
			builder.clientConnector(connector);
			builder.baseUrl(null);
		};
	}

	private ClientHttpConnector buildClientConnector() {
		HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom();
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(org.apache.hc.core5.util.Timeout.ofMilliseconds(1000))
				.setResponseTimeout(org.apache.hc.core5.util.Timeout.ofMilliseconds(2000))
				.build();
		clientBuilder.setDefaultRequestConfig(config);
		return new HttpComponentsClientHttpConnector(clientBuilder.build());
	}

}
