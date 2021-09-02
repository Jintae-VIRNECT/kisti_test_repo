package com.virnect.uaa.domain.auth.account.domain;

import java.util.List;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_otp",
	indexes = {
		@Index(
			name = "index_email",
			columnList="email"
		)
	})
@NoArgsConstructor
@Audited
public class UserOTP {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_otp_id")
	Long id;
	@Transient
	List<Integer> scratchCodes;
	@Column(name = "email", nullable = false)
	private String email;
	@Column(name = "secret_key", nullable = false)
	private String secretKey;
	@Column(name = "validation_code", nullable = false)
	private int validationCode;
}
