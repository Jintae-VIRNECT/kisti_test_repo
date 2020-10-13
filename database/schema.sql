CREATE TABLE recording_files (
    id bigint unsigned AUTO_INCREMENT,
    recording_id varchar(255) NOT NULL UNIQUE,
    session_id varchar(255) NOT NULL,
    workspace_id varchar(255) NOT NULL,
    user_id varchar(255) NOT NULL,
    filename VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
    duration int NOT NULL,
    size int NOT NULL,
    resolution varchar(255) NOT NULL,
    framerate int unsigned NOT NULL,
    meta_data varchar(255),
    created_date DATETIME NOT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL , 
    PRIMARY KEY (id)
    );

CREATE INDEX idx_recording_files_deleted_at ON recording_files(deleted_at); 

CREATE UNIQUE INDEX uix_recording_files_recording_id ON recording_files(recording_id);