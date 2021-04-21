package com.virnect.uaa.domain.user.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_otp")
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
