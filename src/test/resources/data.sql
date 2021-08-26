INSERT INTO `workspace`
(`workspace_id`, `created_at`, `updated_at`, `description`, `name`, `pin_number`, `profile`, `user_id`, `uuid`)
VALUES (138, '2021-08-24 01:48:54','2021-08-24 01:48:54', '설명', '이름', '186507', 'https://192.168.6.3:2838/virnect-platform/workspace/profile/workspace-profile.png', '4b260e69bd6fa9a583c9bbe40f5aceb3', 'MnefhHZkRReFX');

INSERT INTO `workspace_role`
(`workspace_role_id`, `created_at`, `updated_at`, `description`, `role`)
VALUES
    (1, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'MASTER','MASTER')
    ,(2, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'MANAGER','MANAGER')
    ,(3, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'MEMBER','MEMBER')
    ,(4, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'SEAT','SEAT');

INSERT INTO `workspace_permission`
(`workspace_permission_id`, `created_at`, `updated_at`, `description`, `permission`)
VALUES
    (1, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'ALL')
    ,(2, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'MEMBER_MANAGEMENT')
    ,(3, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'GROUP_MANAGEMENT')
    ,(4, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'CONTENTS_MANAGEMENT')
    ,(5, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'PROCESS_MANAGEMENT')
    ,(6, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'NONE');
