package com.virnect.data.repository;

import com.virnect.data.dao.Member;
import com.virnect.data.dao.MemberStatus;
import com.virnect.data.dao.Room;
import com.virnect.data.dao.RoomStatus;
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

            //where
            predicates.add(criteriaBuilder.equal(root.get("roomStatus"), RoomStatus.ACTIVE));

            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("uuid"), userId));

            predicates.add(criteriaBuilder.notEqual(roomJoinMember.get("memberStatus"), MemberStatus.EVICTED));

            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        });
    }

    public static Specification<Room> joinMember(String workspaceId, List<String> userIds, String keyword) {
        return (Specification<Room>) ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Room, Member> roomJoinMember = root.join("members", JoinType.LEFT);

            //where
            predicates.add(criteriaBuilder.equal(root.get("roomStatus"), RoomStatus.ACTIVE));
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));

            predicates.add(criteriaBuilder.notEqual(roomJoinMember.get("memberStatus"), MemberStatus.EVICTED));

            return query
                    .where(criteriaBuilder.in(roomJoinMember.get("uuid")).value(userIds))
                    .where(criteriaBuilder.or(criteriaBuilder.like(root.get("title"), "%" + keyword + "%")))
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        });
    }

    public static Specification<Room> joinMember(String workspaceId, String userId, String keyword) {
        return (Specification<Room>) ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Room, Member> roomJoinMember = root.join("members", JoinType.LEFT);

            //where
            predicates.add(criteriaBuilder.equal(root.get("roomStatus"), RoomStatus.ACTIVE));
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("uuid"), userId));

            predicates.add(criteriaBuilder.notEqual(roomJoinMember.get("memberStatus"), MemberStatus.EVICTED));

            predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));

            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        });
    }
}
