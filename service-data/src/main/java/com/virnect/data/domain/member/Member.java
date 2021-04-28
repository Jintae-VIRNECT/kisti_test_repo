package com.virnect.data.domain.member;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.virnect.data.domain.BaseTimeEntity;
import com.virnect.data.domain.DeviceType;
import com.virnect.data.domain.room.Room;

/**
 * Member Domain Model Class
 * DATE:
 * AUTHOR:
 * EMAIL:
 * DESCRIPTION:
 *
 */
@Entity
@Getter
@Setter
@Table(name = "members")
//@EqualsAndHashCode(of = {"id", "uuid"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "connection_id", nullable = false)
    private String connectionId;

    @Column(name = "member_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Column(name = "device_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "member_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "duration_sec", nullable = false)
    private Long durationSec;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    public Member(
            Room room,
            MemberType memberType,
            String workspaceId,
            String uuid,
            String sessionId
    ) {
        this.room = room;
        this.memberType = memberType;
        this.workspaceId = workspaceId;
        this.uuid = uuid;
        this.sessionId = sessionId;

        // default set
        this.deviceType = DeviceType.UNKNOWN;
        this.memberStatus = MemberStatus.UNLOAD;
        this.connectionId = "";

        LocalDateTime now = LocalDateTime.now();
        this.startDate = now;
        this.endDate = now;
        this.durationSec = 0L;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", memberType='" + memberType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", memberStatus='" + memberStatus + '\'' +
                ", uuid='" + uuid + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
