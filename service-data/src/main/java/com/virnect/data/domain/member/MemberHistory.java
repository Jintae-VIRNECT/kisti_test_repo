package com.virnect.data.domain.member;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.virnect.data.domain.BaseTimeEntity;
import com.virnect.data.domain.DeviceType;
import com.virnect.data.domain.roomhistory.RoomHistory;

@Entity
@Getter
@Setter
@Audited
@Table(name = "members_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_history_id", nullable = false)
    private Long id;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "uuid", nullable = false)
    private String uuid;

   /* @Column(name = "email", nullable = false)
    private String email;*/

    @Column(name = "member_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Column(name = "device_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "duration_sec", nullable = false)
    private Long durationSec;

    @Column(name = "history_deleted", nullable = false)
    private boolean historyDeleted;

    @ManyToOne(targetEntity = RoomHistory.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_history_id")
    private RoomHistory roomHistory;

    @Builder
    public MemberHistory(
            RoomHistory roomHistory,
            MemberType memberType,
            DeviceType deviceType,
            String workspaceId,
            String uuid,
            String sessionId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long durationSec
    ) {
        this.roomHistory = roomHistory;
        this.memberType = memberType;
        this.deviceType = deviceType;
        this.workspaceId = workspaceId;
        this.uuid = uuid;
        //this.email = email;
        this.sessionId = sessionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationSec = durationSec;
        this.historyDeleted = false;
    }

    @Override
    public String toString() {
        return "MemberHistory{" +
                "id=" + id +
                ", memberType='" + memberType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", uuid='" + uuid + '\'' +
                //", email='" + email + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

}
