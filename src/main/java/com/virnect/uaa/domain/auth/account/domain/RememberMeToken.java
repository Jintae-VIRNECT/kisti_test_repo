package com.virnect.uaa.domain.auth.account.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

/**
 * Project: PF-Auth
 * DATE: 2021-03-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RedisHash("RememberMeToken")
public class RememberMeToken {
    private String username;
    @Id
    private String series;
    private String tokenValue;
    private Date date;

    public RememberMeToken(String username, String series, String tokenValue, Date date) {
        this.username = username;
        this.series = series;
        this.tokenValue = tokenValue;
        this.date = date;
    }

    @Override
    public String toString() {
        return "RememberMeToken{" +
                "username='" + username + '\'' +
                ", series='" + series + '\'' +
                ", tokenValue='" + tokenValue + '\'' +
                ", date=" + date +
                '}';
    }
}
