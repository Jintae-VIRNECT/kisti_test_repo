package com.virnect.data.domain.member;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import com.virnect.data.domain.Permission;

@Getter
@Setter
@Audited
@Entity
@Table(name = "member_permission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_permission_id")
    private Long id;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Builder
    public MemberPermission(
            Member member,
            Permission permission){
        //this.member = member;
        this.permission = permission;
    }

}
