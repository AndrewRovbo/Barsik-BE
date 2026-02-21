package com.barsik.backend;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.barsik.backend.api.controller.AuthController;


@Disabled
@SpringBootTest
//@AutoConfigureRestTestClient
class BackendApplicationTest {

	@Autowired
	private AuthController authController; 

	//@Autowired
	//private RestTestClient restTestClient;


    @Test
	void contextLoads() {
		assertThat(authController).isNotNull();
	}

	/*
		@Test
	void greetingShouldReturnDefaultMessage() {
		restTestClient.get().uri("/")
				.exchange()
				.expectBody(String.class)//json request
				.isEqualTo("Hello, World");//object dto
	}
*/
}
