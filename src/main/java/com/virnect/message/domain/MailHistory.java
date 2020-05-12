package com.virnect.message.domain;

import lombok.*;

import javax.persistence.*;

/**
 * Project: PF-Message
 * DATE: 2020-04-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Table(name = "mail_history")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailHistory extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_history_id")
    private Long id;

    @Column(name = "receiver", nullable = false)
    private String receiver;

    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "contents", nullable = false, columnDefinition = "LONGTEXT")
    private String contents;

    @Column(name = "result_code", nullable = false)
    private Integer resultCode;

    @Builder
    public MailHistory(String receiver, String sender, String subject, String contents, Integer resultCode) {
        this.receiver = receiver;
        this.sender = sender;
        this.subject = subject;
        this.contents = contents;
        this.resultCode = resultCode;
    }
}

