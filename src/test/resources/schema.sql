DROP TABLE IF EXISTS process;
DROP TABLE IF EXISTS sub_process;
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS issue;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS target;
DROP TABLE IF EXISTS daily_total;
DROP TABLE IF EXISTS daily_total_workspace;

create table process (
                       process_id bigint primary key,
                       created_at timestamp not null,
                       updated_at timestamp not null,
                       content_manager_uuid varchar(255) not null,
                       content_uuid varchar(255) not null,
                       end_date timestamp,
                       name varchar(50) not null,
                       position varchar(100),
                       reported_date timestamp,
                       start_date timestamp,
                       state varchar(255),
                       workspace_uuid varchar(255) not null
);

create table sub_process (
                           sub_process_id bigint primary key,
                           created_at timestamp not null,
                           updated_at timestamp not null,
                           end_date timestamp,
                           is_recent varchar(255),
                           name varchar(255) not null,
                           priority int(10) not null,
                           reported_date timestamp,
                           start_date timestamp,
                           worker_uuid varchar(255),
                           process_id bigint
);
     
create table job (
                   job_id bigint primary key,
                   created_at timestamp not null,
                   updated_at timestamp not null,
                   is_reported varchar(255),
                   name varchar(255) not null,
                   priority varchar(255) not null,
                   result varchar(255),
                   sub_process_id bigint
);
     
create table report (
                      report_id bigint primary key ,
                      created_at timestamp not null,
                      updated_at timestamp not null,
                      job_id bigint
);
     
create table issue (
                     issue_id bigint primary key,
                     created_at timestamp not null,
                     updated_at timestamp not null,
                     content varchar(255),
                     photo_file_path varchar(255),
                     worker_uuid varchar(255),
                     job_id bigint
);

create table item (
                    item_id bigint primary key,
                    created_at timestamp not null,
                    updated_at timestamp not null,
                    answer varchar(255),
                    photo_file_path varchar(255),
                    priority int(10),
                    result varchar(255),
                    title varchar(255),
                    type varchar(255),
                    report_id bigint

);

create table target (
                      target_id bigint primary key,
                      created_at timestamp not null,
                      updated_at timestamp not null,
                      data varchar(255),
                      type varchar(255),
                      img_path varchar(255),
                      process_id bigint
);

create table daily_total (
                           id bigint primary key,
                           created_at timestamp not null,
                           updated_at timestamp not null,
                           total_count_processes int not null,
                           total_rate int not null
);

create table daily_total_workspace (
                                     id bigint primary key,
                                     created_at timestamp not null,
                                     updated_at timestamp not null,
                                     total_count_processes int not null,
                                     total_rate int not null,
                                     workspace_uuid varchar(255) not null,
                                     daily_total_id bigint

);
