package com.virnect.process.api;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Search Type enumerated class
 */
public enum SearchType {
    PROCESS_NAME,
    SUBPROCESS_NAME,
    JOB_NAME,
    USER_NAME,
    NONE;

    @Override
    public String toString() {
        return "SearchType{}";
    }
}
