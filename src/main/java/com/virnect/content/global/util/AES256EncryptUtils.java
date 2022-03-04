package com.virnect.content.global.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: AES 256 관련 암복호화 유틸
 */
@Slf4j
public class AES256EncryptUtils {
	public static String encrypt(final String key, final String data) {
		try {
			byte[] keyData = key.getBytes(StandardCharsets.UTF_8);
			byte[] ivData = key.substring(0, 16).getBytes(StandardCharsets.UTF_8);
			SecretKey secretKey = new SecretKeySpec(keyData, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivData));

			byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return new String(Base64.getEncoder().encode(encrypted));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
			| InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public static String decrypt(final String key, final String encryptedData) {
		byte[] keyData;
		try {
			keyData = key.getBytes(StandardCharsets.UTF_8);
			byte[] ivData = key.substring(0, 16).getBytes(StandardCharsets.UTF_8);
			SecretKey secretKey = new SecretKeySpec(keyData, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivData));
			byte[] decrypted = Base64.getDecoder().decode(encryptedData.getBytes(StandardCharsets.UTF_8));
			return new String(cipher.doFinal(decrypted), StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
			| InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public static String decryptByBytes(final String key, final String encryptedData) {
		byte[] keyData;
		try {
			byte[] keyOriginBytes = key.getBytes(StandardCharsets.UTF_8);
			byte[] keyBytes = new byte[16];
			System.arraycopy(keyOriginBytes, 0, keyBytes, 0, keyOriginBytes.length);
			//            keyData = key.getBytes(StandardCharsets.UTF_8);
			//            byte[] ivData = key.substring(0, 16).getBytes(StandardCharsets.UTF_8);
			SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(keyBytes));
			byte[] decrypted = Base64.getDecoder().decode(encryptedData.getBytes(StandardCharsets.UTF_8));
			return new String(cipher.doFinal(decrypted), StandardCharsets.UTF_8);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
			| InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			log.error(e.getMessage());
		}

		return null;
	}
}
