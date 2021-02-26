package com.virnect.data.domain;

/*@Entity
@Getter
@Setter
@Audited
@Table(name = "remote_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)*/
public class RemoteHistory {
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "remote_history_id")
    private Long id;*/

    //@ManyToOne(targetEntity = Room.class, fetch = FetchType.LAZY)
    //@JoinColumn(name = "room_id")
    //private Room room;

    /*@Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "message", nullable = false)
    private String message;

    @Builder
    public RemoteHistory(Room room, String uuid, String message) {
        this.room = room;
        this.uuid = uuid;
        this.message = message;
    }*/
}
