package com.virnect.data.dao;

import com.virnect.data.domain.member.Member;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.room.Room;
import com.virnect.data.domain.room.RoomStatus;
import com.virnect.data.domain.session.SessionProperty;
import com.virnect.data.domain.session.SessionType;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class RoomSpecifications {
    public static Specification<Room> joinMember(String workspaceId, String userId) {
        return (Specification<Room>) ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Room, Member> roomJoinMember = root.join("members", JoinType.LEFT);
            Join<Room, SessionProperty> roomSessionPropertyJoin = root.join("sessionProperty", JoinType.INNER);


            Predicate p1 = criteriaBuilder.or(criteriaBuilder.equal(roomJoinMember.get("uuid"), userId),
                    criteriaBuilder.equal(roomSessionPropertyJoin.get("sessionType"), SessionType.OPEN));

            predicates.add(p1);
            //where
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.equal(root.get("roomStatus"), RoomStatus.ACTIVE));
            predicates.add(criteriaBuilder.notEqual(roomJoinMember.get("memberStatus"), MemberStatus.EVICTED));

            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        });
    }

    public static Specification<Room> joinMember(String workspaceId, String userId, List<String> userIds, String keyword) {
        return (Specification<Room>) ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Room, Member> roomJoinMember = root.join("members", JoinType.LEFT);
            Join<Room, SessionProperty> roomSessionPropertyJoin = root.join("sessionProperty", JoinType.INNER);

            Predicate p1 = criteriaBuilder.and(
                    criteriaBuilder.in(roomJoinMember.get("uuid")).value(userIds),
                    criteriaBuilder.equal(roomJoinMember.get("uuid"), userId));

            Predicate p2 = criteriaBuilder.or(p1,
                    criteriaBuilder.equal(roomSessionPropertyJoin.get("sessionType"), SessionType.OPEN));

            predicates.add(p2);
            //where
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.equal(root.get("roomStatus"), RoomStatus.ACTIVE));
            predicates.add(criteriaBuilder.notEqual(roomJoinMember.get("memberStatus"), MemberStatus.EVICTED));
            predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("title"), "%" + keyword + "%")));

            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        });
    }

    public static Specification<Room> joinMember(String workspaceId, String userId, String keyword) {
        return (Specification<Room>) ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Room, Member> roomJoinMember = root.join("members", JoinType.LEFT);
            Join<Room, SessionProperty> roomSessionPropertyJoin = root.join("sessionProperty", JoinType.INNER);

            Predicate p1 = criteriaBuilder.or(criteriaBuilder.equal(roomJoinMember.get("uuid"), userId),
                    criteriaBuilder.equal(roomSessionPropertyJoin.get("sessionType"), SessionType.OPEN));

            predicates.add(p1);

            //where
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.equal(root.get("roomStatus"), RoomStatus.ACTIVE));
            predicates.add(criteriaBuilder.notEqual(roomJoinMember.get("memberStatus"), MemberStatus.EVICTED));
            predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));

            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        });
    }
}
