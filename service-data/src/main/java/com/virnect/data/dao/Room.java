package com.virnect.data.dao;

import com.virnect.data.constraint.LicenseConstants;
import com.virnect.data.constraint.LicenseItem;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Room Domain Model Class
 * DATE:
 * AUTHOR:
 * EMAIL:
 * DESCRIPTION:
 *
 */
@Entity
@Getter
@Setter
@Audited
@Table(name = "rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
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

    @Column(name = "room_status", nullable = false)
    private RoomStatus roomStatus;

    @Column(name = "license_name", nullable = false)
    private String licenseName;

    @Column(name = "active_at")
    private LocalDateTime activeDate;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    //@ElementCollection
    //private Set<Member> members = new HashSet<>();
    private List<Member> members = new ArrayList<>();
    //private Collection<Member> Member;

    //add active or un-active type later

    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private SessionProperty sessionProperty;

    @Builder
    public Room(String sessionId,
                String title,
                String description,
                String profile,
                String leaderId,
                String workspaceId,
                String licenseName,
                int maxUserCount,
                SessionProperty sessionProperty
                      ) {
        this.sessionId = sessionId;
        this.title = title;
        this.description = description;
        this.profile = profile;
        this.leaderId = leaderId;
        this.workspaceId = workspaceId;
        this.licenseName = licenseName;
        this.maxUserCount = maxUserCount;
        this.sessionProperty = sessionProperty;

        //default setting
        this.profile = "default";
        this.roomStatus = RoomStatus.UNACTIVE;
        this.activeDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", workspaceId='" + workspaceId + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
