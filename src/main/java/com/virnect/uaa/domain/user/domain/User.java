package com.virnect.uaa.domain.user.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterMemberRequest;
import com.virnect.uaa.infra.file.Default;

/**
 * Project: user
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@Audited
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long id;

	@Column(name = "uuid", nullable = false, unique = true)
	private String uuid;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "birth", nullable = false)
	private LocalDate birth;

	@Column(name = "description")
	private String description;

	@Column(name = "profile", nullable = false)
	private String profile;

	@Column(name = "service_info", nullable = false)
	private String serviceInfo;

	@Column(name = "join_info", nullable = false)
	private String joinInfo;

	@Column(name = "user_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private UserType userType;

	@Column(name = "login_lock", nullable = false)
	@Enumerated(EnumType.STRING)
	private LoginStatus loginLock = LoginStatus.INACTIVE;

	@Column(name = "market_info_receive", nullable = false)
	@Enumerated(EnumType.STRING)
	private Status marketInfoReceive = Status.REJECT;

	@Column(name = "language", nullable = false)
	@Enumerated(EnumType.STRING)
	private Language language;

	@Column(name = "international_number")
	private String internationalNumber;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "recovery_email")
	private String recoveryEmail;

	@Column(name = "question")
	private String question;

	@Column(name = "answer")
	private String answer;

	@Column(name = "password_update_at")
	private LocalDateTime passwordUpdateDate;

	@Column(name = "account_non_expired")
	private boolean accountNonExpired = true;

	@Column(name = "account_non_locked")
	private boolean accountNonLocked = true;

	@Column(name = "credentials_non_expired")
	private boolean credentialsNonExpired = true;

	@Column(name = "enabled")
	private boolean enabled = true;

	@Column(name = "account_password_initialized")
	private boolean accountPasswordInitialized;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<UserPermission> userPermissionList = new ArrayList<>();

	@Builder(builderMethodName = "createMemberUser")
	public User(
		RegisterMemberRequest registerMemberRequest,
		String encodedPassword
	) {
		this.uuid = RandomStringUtils.randomAlphanumeric(13);
		this.email = registerMemberRequest.getEmail();
		this.password = encodedPassword;
		this.lastName = registerMemberRequest.getEmail();
		this.firstName = "-Member";
		this.name = registerMemberRequest.getEmail() + "-Member";
		this.nickname = registerMemberRequest.getEmail() + "-Member";
		this.profile = Default.USER_PROFILE.getValue();
		this.userType = UserType.USER;
		this.birth =LocalDate.now();
		this.joinInfo = "워크스페이스 멤버 등록";
		this.serviceInfo = "워크스페이스 멤버 등록";
		this.language = Language.KO;
		this.marketInfoReceive = Status.REJECT;
		this.accountPasswordInitialized = false;
	}

	@Builder(builderMethodName = "createNewUser")
	public User(RegisterRequest registerRequest, String encodedPassword, String profileUrl) {
		this.uuid = RandomStringUtils.randomAlphanumeric(13);
		this.email = registerRequest.getEmail();
		this.password = encodedPassword;
		this.name = registerRequest.getName();
		this.profile = profileUrl;
		this.firstName = registerRequest.getFirstName();
		this.lastName = registerRequest.getLastName();
		this.nickname = registerRequest.getNickname();
		this.birth = registerRequest.getBirth();
		this.internationalNumber = registerRequest.getInternationalNumberOfMobile();
		this.mobile = registerRequest.getMobile();
		this.recoveryEmail = registerRequest.getRecoveryEmail();
		this.joinInfo = registerRequest.getJoinInfo();
		this.serviceInfo = registerRequest.getServiceInfo();
		this.marketInfoReceive = Status.valueOf(registerRequest.getMarketInfoReceive());
		this.language = Language.KO;
		this.userType = UserType.USER;
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", uuid='" + uuid + '\'' +
			", name='" + name + '\'' +
			", firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", nickname='" + nickname + '\'' +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			", birth=" + birth +
			", description='" + description + '\'' +
			", profile='" + profile + '\'' +
			", serviceInfo='" + serviceInfo + '\'' +
			", joinInfo='" + joinInfo + '\'' +
			", userType=" + userType +
			", loginLock=" + loginLock +
			", marketInfoReceive=" + marketInfoReceive +
			", language=" + language +
			", internationalNumber='" + internationalNumber + '\'' +
			", mobile='" + mobile + '\'' +
			", recoveryEmail='" + recoveryEmail + '\'' +
			", question='" + question + '\'' +
			", answer='" + answer + '\'' +
			", passwordUpdateDate=" + passwordUpdateDate +
			", accountNonExpired=" + accountNonExpired +
			", accountNonLocked=" + accountNonLocked +
			", credentialsNonExpired=" + credentialsNonExpired +
			", enabled=" + enabled +
			", accountPasswordInitialized=" + accountPasswordInitialized +
			'}';
	}
}
