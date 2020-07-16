-- alter table content
--     drop constraint if exists UK_jn2aim0jb1d6e3sr2cvb2xqka;
--
-- -- alter table content
-- --     add constraint UK_jn2aim0jb1d6e3sr2cvb2xqka unique (path);
--
-- alter table content
--     drop constraint if exists UK_pgh7o2xojogt3mlokbys4n5dh;
--
-- -- alter table content
-- --     add constraint UK_pgh7o2xojogt3mlokbys4n5dh unique (uuid);
--
-- alter table target
--     drop constraint if exists UK_qfmf6u3v5j89khqji4g1y89fe;

-- alter table target
--     add constraint UK_qfmf6u3v5j89khqji4g1y89fe unique (data);

-- alter table content
--     add constraint FKc1ec2my3cfwu9xajluo9caxla
--         foreign key (type_id)
--             references type;
--
-- alter table scene_group
--     drop constraint if exists FKi4qlfnw4j5w3q33ukv12s1ryf;
--         foreign key (content_id)
--             references content;

-- alter table target
--     add constraint FK7q1h08s52hlk33b82ltuarvbs
--         foreign key (content_id)
--             references content;
--
-- alter table type_device
--     add constraint FKdngnh35h45grpes62d8n0ul3t
--         foreign key (type_id)
--             references type;

DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS scene_group;
DROP TABLE IF EXISTS target;
DROP TABLE IF EXISTS type;
DROP TABLE IF EXISTS type_device;

create table content (
                         content_id bigint primary key,
                         created_at timestamp not null,
                         updated_at timestamp not null,
                         converted varchar(255) not null,
                         deleted varchar(255) not null,
                         download_hits bigint,
                         metadata clob not null,
                         name varchar(255) not null,
                         path varchar(255) not null,
                         properties clob not null,
                         shared varchar(255) not null,
                         size bigint not null,
                         user_uuid varchar(255) not null,
                         uuid varchar(255) not null,
                         workspace_uuid varchar(255) not null,
                         type_id bigint
);

create table scene_group (
                             scene_group_id bigint primary key,
                             created_at timestamp not null,
                             updated_at timestamp not null,
                             job_total integer,
                             name varchar(255),
                             priority integer,
                             uuid varchar(255),
                             content_id bigint
);
     
create table target (
                        target_id bigint primary key,
                        created_at timestamp not null,
                        updated_at timestamp not null,
                        data varchar(255),
                        img_path varchar(255),
                        type varchar(255),
                        content_id bigint
);
     
create table type (
                      type_id bigint primary key ,
                      created_at timestamp not null,
                      updated_at timestamp not null,
                      type varchar(255)
);
     
create table type_device (
                             type_device_id bigint primary key ,
                             created_at timestamp not null,
                             updated_at timestamp not null,
                             name varchar(255),
                             type_id bigint
);