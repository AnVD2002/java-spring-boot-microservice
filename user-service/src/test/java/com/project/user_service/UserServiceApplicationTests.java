package com.project.user_service;

import com.project.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
class UserServiceApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void searchUsersForAdminWithoutKeyword() {
		userRepository.searchForAdmin(
				"",
				null,
				PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "insertedAt"))
		);
	}

}
