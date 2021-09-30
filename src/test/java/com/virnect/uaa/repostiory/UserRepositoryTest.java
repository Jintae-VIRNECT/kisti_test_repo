package com.virnect.uaa.repostiory;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;

@ExtendWith(SpringExtension.class)
// @DataJpaTest
@SpringBootTest
@ActiveProfiles("test")
// @Import(JpaTestConfiguration.class)
public class UserRepositoryTest {
	List<String> userUUIDList = Arrays.asList(
		"4122a7b8e1d31aee921d36298b7e8709", "rtR0MD7SXx4Du", "2JzEzFpeoyFGO", "NEJqNNdPUTxUT", "YtRO0fhoUlYax", "9ll0ulGiX53RB",
		"2HhRIGWBF93qA", "haL4NHlQJ8SU7", "wsigZkkAK5blx","5m3EsEVyQ05sw", "Si0JileQIYUO5", "iJL2m319R3Hxl"
	);
	@Autowired
	private UserRepository userRepository;
	private String search = "test";

	@Test
	@DisplayName("userRepository 검색어 동반 동적 쿼리 테스트 - 검색어 있음")
	public void findUserInfoWithUUIDListAndSearchQuery() {
		List<User> users = userRepository.findUserInformationInUUIDListWithSearchQuery(userUUIDList, "test");
		for (User user : users) {
			assertThat(user.getEmail().contains(search) || user.getNickname().contains(search)).isTrue();
		}
		assertThat(users.size()).isEqualTo(5);
	}

	@Test
	@DisplayName("userRepository 검색어 동반 동적 쿼리 테스트 - 검색어 없음")
	public void findUserINfoWithUUIDListOnly() {
		List<User> users = userRepository.findUserInformationInUUIDListWithSearchQuery(userUUIDList, null);
		assertThat(users.size()).isEqualTo(12);
	}
}
