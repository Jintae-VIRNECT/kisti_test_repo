INSERT INTO `workspace_role`
(`workspace_role_id`, `created_at`, `updated_at`, `description`, `role`)
VALUES
    (1, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'MASTER','MASTER')
    ,(2, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'MANAGER','MANAGER')
    ,(3, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'MEMBER','MEMBER')
    ,(4, '2020-01-14 11:29:30', '2020-01-14 11:29:30', 'GUEST','GUEST');

INSERT INTO `workspace_permission`
(`workspace_permission_id`, `created_at`, `updated_at`, `description`, `permission`)
VALUES
    (1, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'ALL')
    ,(2, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'MEMBER_MANAGEMENT')
    ,(3, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'GROUP_MANAGEMENT')
    ,(4, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'CONTENTS_MANAGEMENT')
    ,(5, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'PROCESS_MANAGEMENT')
    ,(6, '2020-01-14 11:30:48', '2020-01-14 11:30:48', null, 'NONE');

INSERT INTO `workspace` (`workspace_id`, `created_at`, `updated_at`, `description`, `name`, `pin_number`, `profile`, `user_id`, `uuid`)
VALUES (1, '2020-03-31 09:21:35', '2021-09-29 07:50:18', '워크스페이스 입니다.', 'USERs Workspace', '377516', '', '498b1839dc29ed7bb2ee90ad6985c608', '4d6eab0860969a50acbfa4599fbb5ae8');

INSERT INTO `workspace_user` (`workspace_user_id`, `created_at`, `updated_at`, `user_id`, `workspace_id`)
VALUES (1, '2020-03-31 09:21:36', '2020-03-31 09:21:36', '498b1839dc29ed7bb2ee90ad6985c608', 1);
INSERT INTO `workspace_user` (`workspace_user_id`, `created_at`, `updated_at`, `user_id`, `workspace_id`)
VALUES (964, '2021-06-16 05:13:15', '2021-06-16 05:13:15', '273435b53ca1462d9e8ec58b78e5ad22', 1);

INSERT INTO `workspace_user_permission` (`workspace_user_permission_id`, `created_at`, `updated_at`, `workspace_permission_id`, `workspace_role_id`, `workspace_user`)
VALUES (1, '2020-03-31 09:21:36', '2020-03-31 09:21:36', 1, 1, 1);
INSERT INTO `workspace_user_permission` (`workspace_user_permission_id`, `created_at`, `updated_at`, `workspace_permission_id`, `workspace_role_id`, `workspace_user`)
VALUES (947, '2021-06-16 05:13:15', '2021-08-25 08:17:28', 1, 3, 964);
