INSERT INTO `os` (`os_id`, `created_at`, `updated_at`, `name`) VALUES (1, '2020-06-05 10:07:02', '2020-06-05 10:07:02', 'IOS');
INSERT INTO `os` (`os_id`, `created_at`, `updated_at`, `name`) VALUES (2, '2020-06-05 10:07:02', '2020-06-05 10:07:02', 'ANDROID');
INSERT INTO `os` (`os_id`, `created_at`, `updated_at`, `name`) VALUES (3, '2020-06-05 10:07:02', '2020-06-05 10:07:02', 'WINDOWS');

INSERT INTO `device_type` (`device_type_id`, `created_at`, `updated_at`, `name`) VALUES (1, '2020-07-23 11:58:55', '2020-07-23 11:58:55', 'PC');
INSERT INTO `device_type` (`device_type_id`, `created_at`, `updated_at`, `name`) VALUES (2, '2020-07-23 11:58:55', '2020-07-23 11:58:55', 'Realwear');
INSERT INTO `device_type` (`device_type_id`, `created_at`, `updated_at`, `name`) VALUES (3, '2020-07-23 11:58:55', '2020-07-23 11:58:55', 'Google Play');

INSERT INTO `device` (`device_id`, `created_at`, `updated_at`, `name`, `device_type_id`) VALUES (1, '2020-07-23 11:59:52', '2020-07-23 11:59:53', '<span style="color: #1468e2">Window</span>용 설치 파일', 1);
INSERT INTO `device` (`device_id`, `created_at`, `updated_at`, `name`, `device_type_id`) VALUES (2, '2020-07-23 11:59:52', '2020-07-23 11:59:53', '<span style="color: #1468e2">HMT-1</span>용 APK', 2);
INSERT INTO `device` (`device_id`, `created_at`, `updated_at`, `name`, `device_type_id`) VALUES (3, '2020-07-23 11:59:52', '2020-07-23 11:59:53', '<span style="color: #1468e2">스마트폰/타블릿</span>', 3);

INSERT INTO `app` (`app_id`, `created_at`, `updated_at`, `app_download_count`, `app_url`, `guide_download_count`, `guide_url`, `image_url`, `product`, `uuid`, `version`, `device_id`, `os_id`) VALUES (1, '2020-07-24 10:07:33', '2020-07-24 10:07:33', 0, 'https://file.virnect.com/app/v0.7.2_remote_android_realwear_20200724.apk', 0, NULL, 'https://file.virnect.com/resource/view_realwear.png', 'REMOTE', 'b114-cbc236940470', '0.7.2', 2, 2);
INSERT INTO `app` (`app_id`, `created_at`, `updated_at`, `app_download_count`, `app_url`, `guide_download_count`, `guide_url`, `image_url`, `product`, `uuid`, `version`, `device_id`, `os_id`) VALUES (2, '2020-07-24 10:07:33', '2020-07-24 10:07:33', 0, 'https://file.virnect.com/app/v2.0.13.02_make_window_pc_20200724.exe', 0, NULL, 'https://file.virnect.com/resource/make_pc.png', 'MAKE', '05d1-8795535abb40', '2.0.13.02', 1, 3);
INSERT INTO `app` (`app_id`, `created_at`, `updated_at`, `app_download_count`, `app_url`, `guide_download_count`, `guide_url`, `image_url`, `product`, `uuid`, `version`, `device_id`, `os_id`) VALUES (3, '2020-07-24 10:07:33', '2020-07-24 10:07:33', 0, 'https://file.virnect.com/app/v2.0.13.02_view_android_pc_20200724.apk', 0, NULL, 'https://file.virnect.com/resource/view_android.png', 'VIEW', 'ed60-16991ea785f0', '2.0.13.02', 3, 3);
INSERT INTO `app` (`app_id`, `created_at`, `updated_at`, `app_download_count`, `app_url`, `guide_download_count`, `guide_url`, `image_url`, `product`, `uuid`, `version`, `device_id`, `os_id`) VALUES (4, '2020-07-24 10:07:33', '2020-07-24 10:07:33', 0, 'https://file.virnect.com/app/v2.0.13.02_view_android_realwear_20200724.apk', 0, NULL, 'https://file.virnect.com/resource/view_realwear.png', 'VIEW', 'b114-cbc236940471', '2.0.13.02', 2, 2);


---
