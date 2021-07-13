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
@ActiveProfiles("local")
// @Import(JpaTestConfiguration.class)
public class UserRepositoryTest {
	List<String> userUUIDList = Arrays.asList(
		"0IUyG1hy7MjcB", "1udr3mGOVPCaO", "22lhpivqs7Idu", "2F9IyiZYBak8n", "2HhRIGWBF93qA", "2JzEzFpeoyFGO",
		"2Xv8spIdcJ8aQ", "3HEZQmdrzBNXN", "40247ff4cbe04a1e8ae3203298996f4c", "40467b0c2dd94a83a8c69d70fc54038d",
		"405893f61bec9ac295ca5fa270baca7f", "40ced71799059341b7e57b85b10ee350", "410df50ca6e32db0b6acba09bcb457ff",
		"4122a7b8e1d31aee921d36298b7e8709", "4163b24f04b699efb817fa2df192456a", "4163b24f04b699efb817fa2df192456b",
		"4163b24f04b699efb817fa2df192456d", "4163b24f04b699efb817fa2df19245ss", "41931ae6c664b43ab675abb778a8dc21",
		"4218059539d944fca0a27fc5a57ce05b", "4250001664a1b08486700a10a90a1af1", "42ca762ee245403ea35743fd8a690918",
		"434d2aab3bb927fca3d7a9666088ae38", "449ae69cee53b8a6819053828c94e496", "45004c06dd6dd12cb85d99272d2b0d31",
		"4549b97621964b3789034168c8f99714", "45d974ac8cbd3f66b26b0179604d6742", "4705cf50e6d02c59b0eef9591666e2a3",
		"473b12854daa6afeb9e505551d1b2743", "478f771762d0b3e29fcb901e4b998d9d", "487d76f9395db05f9ce40a8a12236b90",
		"489e6ecfb9197afb9d0c55832a0ad832", "498b1839dc29ed7bb2ee90ad6985c608", "49b89cb0120dc65ba8cb962789682f64",
		"4a38d2a83bffc82ebcb1f4b2fc68bc05", "4a65aa94523efe5391b0541bbbcf97a3", "4a8a6883572afb9b866918f3fbb5c0b5",
		"4acc8c809650af9c89022329ef36e22d", "4ae689a36215c1b894aada41a4d67f59", "4b260e69bd6fa9a583c9bbe40f5aceb3",
		"4bae0a42edd4306b8c2641ebf565c050", "4bdb504dd1b2b49789c4d2dc13b4c5b3", "4bf5cb53ad7723b8a1905fac5225703c",
		"4c9b9bfc0ec7c74781a19331438acdc8", "4d0c92f824a863ac8a6cd9af03c320d5", "4d127135f699616fb88e6bc4fa6d784a",
		"4d3154962003a069be27260a818380a7", "4d8d02b431ccbccbae93553245511232", "4d8d02b431ccbccbae9355324551123e",
		"4d8d02b431ccbccbae9355324551131e", "4dbcb731ed45c4388ae7e880c28abea1", "4DkWoZeRW3dQW",
		"4e6793e995dc57cfb129f8b03fbe7737", "4ea61b4ad1dab12fb2ce8a14b02b7460", "4ea7ff121f591b56a5af14a31690641f",
		"4f941bb7436616a583bc7e6dc7c8e201", "4ffd52e92874da7392ffdf103c8d987e", "5m3EsEVyQ05sw", "6CFQ6CgpQOFz1",
		"7S6mZEfJL4Zdi", "9ll0ulGiX53RB", "a1CzZmFlbttB9", "D2AcddRUfPX1M", "DT9Ozipala5fd", "EWBsQcU9wWwhB",
		"FHclcplbv70cY", "ge85sofOJh2kE", "GkcpsdQsO3sjR", "GNG0UhluPOOEq", "GUGvOc8QFmfaT", "haL4NHlQJ8SU7",
		"hu9cIBAPagi5Z", "I7EtZ97rKGWEY", "iJL2m319R3Hxl", "iK50ZTc6dVE1D", "KfJGSdfzUtoak", "KIREdNHDxoHNc",
		"LfE562RhxQjfP", "MKRskbzQnZj54", "mVM1kbIGp5mA3", "NbE4iPofKAZGN", "NEJqNNdPUTxUT", "oRyBj6IVfPWaa",
		"pYZyLARxTuvQV", "Rsom9eYRYaziF", "rtR0MD7SXx4Du", "SGTYvjuzS04uL", "Si0JileQIYUO5", "socKxMPdnCvNU",
		"Tgi8rbJay1s0u", "TIFIFxvncu9bo", "Ueic4lVQYXaTl", "WFPA265VgjIWx", "wMYOqNOZTrMuh", "wsigZkkAK5blx",
		"WTK8lRnDAQ4fx", "XaznpMANVCaBO", "XfgFDkJCfWxkq", "xsX9bzfZNTgRw", "yPrYvlgTKuoo6", "YtRO0fhoUlYax",
		"yYqW1ZujSTDGh", "yzUesA6kR2xbx", "zlQeYRg8ty1cY"
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
		assertThat(users.size()).isEqualTo(32);
	}

	@Test
	@DisplayName("userRepository 검색어 동반 동적 쿼리 테스트 - 검색어 없음")
	public void findUserINfoWithUUIDListOnly() {
		List<User> users = userRepository.findUserInformationInUUIDListWithSearchQuery(userUUIDList, null);
		assertThat(users.size()).isEqualTo(104);
	}
}
