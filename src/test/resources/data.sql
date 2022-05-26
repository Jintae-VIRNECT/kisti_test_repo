INSERT INTO `content` (`content_id`, `created_at`, `updated_at`, `converted`, `deleted`, `metadata`, `name`, `path`,
                       `properties`, `shared`, `size`, `user_uuid`, `uuid`, `workspace_uuid`, `type_id`,
                       `download_hits`)
VALUES (601, '2021-12-06 05:23:24', '2021-12-06 05:23:24', 'NO', 'NO', '', 'DPLA-3909',
        'https://192.168.6.3:2838/virnect-platform/workspace/4560ff80baf346af946f48b037f5af6b/content/3ac931f7-5b3b-4807-ac6e-61ae5d138204.Ares',
        '{"TargetID":"24d312d9-845c-4349-9892-fc9e46322f6e","TargetSize":10.0,"PropertyInfo":{"ff885ac9-1d28-4849-9b40-42a392660724":{"PropertyInfo":{"sceneGroupTitle":"씬 그룹 1","sceneGroupDetail":"","ComponentName":"씬 그룹 1","ComponentType":2,"identifier":"ff885ac9-1d28-4849-9b40-42a392660724"},"Child":{"d88b0128-674f-446d-af84-783272e73313":{"PropertyInfo":{"sceneTitle":"","sceneType":1,"sceneDetail":"","ComponentName":"씬 3","ComponentType":3,"identifier":"d88b0128-674f-446d-af84-783272e73313"},"Child":{"aa130b3b-8724-4eba-9b8c-ee2f37858687":{"PropertyInfo":{"text":"원점","font":"NotoSansCJKkr-Bold","fontSize":32,"alignment":3,"color":{"r":0.9803922,"g":0.3921569,"b":0.0,"a":1.0},"shadow":0.0,"ComponentName":"텍스트 1","ComponentType":101,"identifier":"aa130b3b-8724-4eba-9b8c-ee2f37858687"}},"372fcd03-33f1-4828-9a2a-26b8d1e5e6d6":{"PropertyInfo":{"text":"10cm","font":"NotoSansCJKkr-Bold","fontSize":32,"alignment":3,"color":{"r":0.9803922,"g":0.3921569,"b":0.0,"a":1.0},"shadow":0.0,"ComponentName":"텍스트 1 Copy 1","ComponentType":101,"identifier":"372fcd03-33f1-4828-9a2a-26b8d1e5e6d6"}},"e75df63d-31bf-41f7-b8da-96ef29ba0ede":{"PropertyInfo":{"text":"뒤","font":"NotoSansCJKkr-Bold","fontSize":32,"alignment":3,"color":{"r":0.9803922,"g":0.3921569,"b":0.0,"a":1.0},"shadow":0.0,"ComponentName":"텍스트 2","ComponentType":101,"identifier":"e75df63d-31bf-41f7-b8da-96ef29ba0ede"}}}}}}}}',
        'YES', 1384, '4a65aa94523efe5391b0541bbbcf97a3', '3ac931f7-5b3b-4807-ac6e-61ae5d138204',
        '4560ff80baf346af946f48b037f5af6b', NULL, 0);

INSERT INTO `target` (`target_id`, `created_at`, `updated_at`, `data`, `type`, `img_path`, `content_id`, `size`)
VALUES (514, '2021-12-06 05:23:24', '2021-12-06 05:23:24', 'swQPO%2fS%2fQSDOWIyQFRMTxQdfjrnPdR0HfhGyiduajtM%3d',
        'VTarget', 'https://192.168.6.3:2838/virnect-platform/workspace/report/virnect_target.png', 601, 10);


INSERT INTO `scene_group` (`scene_group_id`, `created_at`, `updated_at`, `job_total`, `name`, `priority`, `uuid`,
                           `content_id`)
VALUES (11210, '2021-12-06 05:23:24', '2021-12-06 05:23:24', 1, '씬 그룹 1', 1, 'ff885ac9-1d28-4849-9b40-42a392660724',
        601);

INSERT INTO `project` (`project_id`, `created_at`, `updated_at`, `edit_permission`, `name`, `path`, `properties`,
                       `share_permission`, `size`, `uuid`, `workspace_uuid`, `user_uuid`)
VALUES (143, '2022-01-13 04:01:55', '2022-01-13 04:01:55', 'MEMBER', '프로젝트',
        'https://192.168.6.3:2838/virnect-platform/workspace/4d6eab0860969a50acbfa4599fbb5ae8/project/10ecd5af-4ac8-4811-a157-0964cfae7725.Mars',
        '{"propertyName":"프로젝트 이름","propertyObjectList":[{"objectName":"1-depth 첫번째 씬그룹","objectType":"SceneGroup","objectChildList":[{"objectName":"2-depth 씬","objectType":"Scene","objectChildList":[{"objectName":"3-depth 오브젝트111","objectType":"Text","objectChildList":null}]}]}]}',
        'MEMBER', 1551, '10ecd5af-4ac8-4811-a157-0964cfae7725', '4d6eab0860969a50acbfa4599fbb5ae8',
        '498b1839dc29ed7bb2ee90ad6985c608');
INSERT INTO `project` (`project_id`, `created_at`, `updated_at`, `edit_permission`, `name`, `path`, `properties`,
                       `share_permission`, `size`, `uuid`, `workspace_uuid`, `user_uuid`)
VALUES (144, '2022-01-13 04:02:18', '2022-01-13 04:02:18', 'MEMBER', '프로젝트',
        'https://192.168.6.3:2838/virnect-platform/workspace/4d6eab0860969a50acbfa4599fbb5ae8/project/2b7ee0c0-88c5-4b03-8850-614e085dba65.Mars',
        '{"propertyName":"프로젝트 이름","propertyObjectList":[{"objectName":"1-depth 첫번째 씬그룹","objectType":"SceneGroup","objectChildList":[{"objectName":"2-depth 씬","objectType":"Scene","objectChildList":[{"objectName":"3-depth 오브젝트111","objectType":"Text","objectChildList":null}]}]}]}',
        'UPLOADER', 1551, '2b7ee0c0-88c5-4b03-8850-614e085dba65', '4d6eab0860969a50acbfa4599fbb5ae8',
        '498b1839dc29ed7bb2ee90ad6985c608');
INSERT INTO `project` (`project_id`, `created_at`, `updated_at`, `edit_permission`, `name`, `path`, `properties`,
                       `share_permission`, `size`, `uuid`, `workspace_uuid`, `user_uuid`)
VALUES (145, '2022-01-13 04:19:07', '2022-01-13 04:19:07', 'MEMBER', '프로젝트',
        'https://192.168.6.3:2838/virnect-platform/workspace/4d6eab0860969a50acbfa4599fbb5ae8/project/08aa5e30-ca07-4dd0-9a4d-ba46dd3da4d6.Mars',
        '{"propertyName":"프로젝트 이름","propertyObjectList":[{"objectName":"1-depth 첫번째 씬그룹","objectType":"SceneGroup","objectChildList":[{"objectName":"2-depth 씬","objectType":"Scene","objectChildList":[{"objectName":"3-depth 오브젝트111","objectType":"Text","objectChildList":null}]}]}]}',
        'MANAGER', 1551, '08aa5e30-ca07-4dd0-9a4d-ba46dd3da4d6', '4d6eab0860969a50acbfa4599fbb5ae8',
        '498b1839dc29ed7bb2ee90ad6985c608');

INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (450, '2022-01-13 04:01:55', '2022-01-13 04:01:55', 'TWO_DIMENSINAL', 143);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (451, '2022-01-13 04:01:55', '2022-01-13 04:01:55', 'THREE_DIMENSINAL', 143);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (452, '2022-01-13 04:01:55', '2022-01-13 04:01:55', 'TWO_OR_THREE_DIMENSINAL', 143);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (453, '2022-01-13 04:02:18', '2022-01-13 04:02:18', 'TWO_DIMENSINAL', 144);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (454, '2022-01-13 04:02:18', '2022-01-13 04:02:18', 'THREE_DIMENSINAL', 144);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (455, '2022-01-13 04:02:18', '2022-01-13 04:02:18', 'TWO_OR_THREE_DIMENSINAL', 144);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (456, '2022-01-13 04:19:07', '2022-01-13 04:19:07', 'TWO_DIMENSINAL', 145);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (457, '2022-01-13 04:19:07', '2022-01-13 04:19:07', 'THREE_DIMENSINAL', 145);
INSERT INTO `project_mode` (`project_mode_id`, `created_at`, `updated_at`, `mode`, `project_id`)
VALUES (458, '2022-01-13 04:19:07', '2022-01-13 04:19:07', 'TWO_OR_THREE_DIMENSINAL', 145);

INSERT INTO `project_target` (`project_target_id`, `created_at`, `updated_at`, `length`, `path`, `type`, `width`,
                              `project_id`, `data`)
VALUES (146, '2022-01-13 04:01:55', '2022-01-13 04:01:55', 10,
        'https://192.168.6.3:2838/virnect-platform/workspace/report/virnect_target.png', 'VTarget', 10, 143,
        '0f518d23-9226-4c8d-a488-c6581ef90456');
INSERT INTO `project_target` (`project_target_id`, `created_at`, `updated_at`, `length`, `path`, `type`, `width`,
                              `project_id`, `data`)
VALUES (147, '2022-01-13 04:02:18', '2022-01-13 04:02:18', 10, NULL, 'VR', 10, 144,
        '0f518d23-9226-4c8d-a488-c6581ef90456');
INSERT INTO `project_target` (`project_target_id`, `created_at`, `updated_at`, `length`, `path`, `type`, `width`,
                              `project_id`, `data`)
VALUES (148, '2022-01-13 04:19:07', '2022-01-13 04:19:07', 10,
        'https://192.168.6.3:2838/virnect-platform/workspace/4d6eab0860969a50acbfa4599fbb5ae8/report/2022-01-13_moiwgbo8yp.png',
        'QR', 10, 145, '0f518d23-9226-4c8d-a488-c6581ef90456');

INSERT INTO `content` (`content_id`, `created_at`, `updated_at`, `converted`, `deleted`, `metadata`, `name`, `path`,
                       `properties`, `shared`, `size`, `user_uuid`, `uuid`, `workspace_uuid`, `type_id`,
                       `download_hits`)
VALUES (602, '2021-12-10 01:47:52', '2021-12-10 01:47:52', 'NO', 'NO', NULL, '씬없는 컨텐츠',
        'https://192.168.6.3:2838/virnect-platform/workspace/4d6eab0860969a50acbfa4599fbb5ae8/content/1016b7fe-88b2-497f-a04c-98be8fbcccce.Ares',
        '{"TargetID":"4d601597-db60-4d72-a475-11554b4ea6f5","TargetSize":10.0,"PropertyInfo":{}}', 'YES', 565,
        '498b1839dc29ed7bb2ee90ad6985c608', '1016b7fe-88b2-497f-a04c-98be8fbcccce', '4d6eab0860969a50acbfa4599fbb5ae8',
        NULL, 0);
INSERT INTO `content` (`content_id`, `created_at`, `updated_at`, `converted`, `deleted`, `metadata`, `name`, `path`,
                       `properties`, `shared`, `size`, `user_uuid`, `uuid`, `workspace_uuid`, `type_id`,
                       `download_hits`)
VALUES (621, '2021-12-17 05:34:47', '2021-12-17 05:34:47', 'YES', 'NO', NULL, '씬없는 컨텐츠',
        'https://192.168.6.3:2838/virnect-platform/workspace/4d6eab0860969a50acbfa4599fbb5ae8/content/557aec82-18ec-464d-8723-5b699a1910fd.Ares',
        '{"TargetID":"4d601597-db60-4d72-a475-11554b4ea6f5","TargetSize":10.0,"PropertyInfo":{}}', 'NO', 565,
        '498b1839dc29ed7bb2ee90ad6985c608', '557aec82-18ec-464d-8723-5b699a1910fd', '4d6eab0860969a50acbfa4599fbb5ae8',
        NULL, 0);


INSERT INTO `live_share_room` (`live_share_room_id`, `created_at`, `updated_at`, `content_uuid`, `status`,
                               `workspace_uuid`)
VALUES (3, '2022-02-09 05:29:09', '2022-02-09 05:29:09', '1016b7fe-88b2-497f-a04c-98be8fbcccce', 'ACTIVE',
        '4d6eab0860969a50acbfa4599fbb5ae8');
INSERT INTO `live_share_room` (`live_share_room_id`, `created_at`, `updated_at`, `content_uuid`, `status`,
                               `workspace_uuid`)
VALUES (4, '2022-02-09 05:29:09', '2022-02-09 05:29:09', '557aec82-18ec-464d-8723-5b699a1910fd', 'ACTIVE',
        '4d6eab0860969a50acbfa4599fbb5ae8');
INSERT INTO `live_share_user` (`live_share_user_id`, `created_at`, `updated_at`, `room_id`, `status`, `user_email`,
                               `user_nickname`, `user_role`, `user_uuid`)
VALUES (20, '2022-02-09 05:29:09', '2022-02-09 05:29:09', 3, 'ACTIVE', NULL, NULL, 'LEADER',
        '498b1839dc29ed7bb2ee90ad6985c608');
INSERT INTO `live_share_user` (`live_share_user_id`, `created_at`, `updated_at`, `room_id`, `status`, `user_email`,
                               `user_nickname`, `user_role`, `user_uuid`)
VALUES (21, '2022-02-09 05:29:09', '2022-02-09 05:29:09', 3, 'ACTIVE', NULL, NULL, 'FOLLOWER',
        'ouXLjBs6Rmd95');
INSERT INTO `live_share_user` (`live_share_user_id`, `created_at`, `updated_at`, `room_id`, `status`, `user_email`,
                               `user_nickname`, `user_role`, `user_uuid`)
VALUES (22, '2022-02-09 05:29:09', '2022-02-09 05:29:09', 3, 'ACTIVE', NULL, NULL, 'FOLLOWER',
        'XpzzPw8dQOjgS');
INSERT INTO `live_share_user` (`live_share_user_id`, `created_at`, `updated_at`, `room_id`, `status`, `user_email`,
                               `user_nickname`, `user_role`, `user_uuid`)
VALUES (23, '2022-02-09 05:29:09', '2022-02-09 05:29:09', 4, 'ACTIVE', NULL, NULL, 'LEADER',
        'ouXLjBs6Rmd95');