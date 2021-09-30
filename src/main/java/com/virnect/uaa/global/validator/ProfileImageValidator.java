package com.virnect.uaa.global.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProfileImageValidator implements ConstraintValidator<ProfileImageValidate, MultipartFile> {
	private static final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "png", "JPG", "PNG");

	@Override
	public void initialize(ProfileImageValidate constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(MultipartFile profile, ConstraintValidatorContext context) {
		if (profile == null) {
			log.info("MultipartFile image is empty.");
			return true;
		}
		int dotIndex = profile.getOriginalFilename().lastIndexOf('.');
		String extension = dotIndex == -1 ? "" : profile.getOriginalFilename().substring(dotIndex + 1);
		return PROFILE_IMAGE_ALLOW_EXTENSION.contains(extension);
	}
}
