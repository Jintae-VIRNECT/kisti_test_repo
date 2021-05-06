package com.virnect.data.domain.roomhistory;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.virnect.data.domain.BaseTimeEntity;
import com.virnect.data.domain.session.SessionPropertyHistory;
import com.virnect.data.domain.member.MemberHistory;

@Entity
@Getter
@Setter
@Audited
@Table(name = "rooms_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomHistory extends BaseTimeEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "room_history_id", nullable = false)
        private Long id;

        @Column(name = "session_id", unique = true)
        private String sessionId;

        @Column(name = "title", nullable = false)
        private String title;

        @Column(name = "description")
        private String description;

        @Column(name = "profile")
        private String profile;

        @Column(name = "leader_id", nullable = false)
        private String leaderId;

        @Column(name = "workspace_id", nullable = false)
        private String workspaceId;

        @Column(name = "maxUserCount", nullable = false)
        private int maxUserCount;

        @Column(name = "license_name", nullable = false)
        private String licenseName;

        @Column(name = "active_at", nullable = false)
        private LocalDateTime activeDate;

        @Column(name = "unactive_at", nullable = false)
        private LocalDateTime unactiveDate;

        @Column(name = "duration_sec", nullable = false)
        private Long durationSec;

        @OneToMany(mappedBy = "roomHistory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<MemberHistory> memberHistories = new ArrayList<>();

        @OneToOne(mappedBy = "roomHistory", cascade = CascadeType.ALL, orphanRemoval = true)
        private SessionPropertyHistory sessionPropertyHistory;

        @Builder
        public RoomHistory(String sessionId,
                           String title,
                           String description,
                           String profile,
                           String leaderId,
                           String workspaceId,
                           String licenseName,
                           int maxUserCount,
                           SessionPropertyHistory sessionPropertyHistory
        ) {
                this.sessionId = sessionId;
                this.title = title;
                this.description = description;
                this.profile = profile;
                this.leaderId = leaderId;
                this.workspaceId = workspaceId;
                this.licenseName = licenseName;
                this.maxUserCount = maxUserCount;
                this.sessionPropertyHistory = sessionPropertyHistory;
        }

        @Override
        public String toString() {
                return "RoomHistory{" +
                        "id=" + id +
                        ", sessionId='" + sessionId + '\'' +
                        ", title='" + title + '\'' +
                        ", description='" + description + '\'' +
                        ", leaderId='" + leaderId + '\'' +
                        ", workspaceId='" + workspaceId + '\'' +
                        ", profile='" + profile + '\'' +
                        ", durationTime='" + durationSec + '\'' +
                        '}';
        }
}
