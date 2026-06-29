package com.project.auth_service;

import com.project.auth_service.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
class AuthServiceApplicationTests {

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void searchAccountsForAdminWithoutKeyword() {
		accountRepository.searchForAdmin(
				"",
				null,
				PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "insertedAt"))
		);
	}

}
