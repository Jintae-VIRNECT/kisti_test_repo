package com.virnect.data.domain.room;

import com.virnect.data.domain.BaseTimeEntity;

/*@Entity
@Getter
@Setter
@Audited
@Table(name = "room_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)*/
public class RoomMember extends BaseTimeEntity {

   /* @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_member_id")
    private Long id;*/

    /*@ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public RoomMember(Room room, Member member) {
        this.room = room;
        this.member = member;
    }*/
}
