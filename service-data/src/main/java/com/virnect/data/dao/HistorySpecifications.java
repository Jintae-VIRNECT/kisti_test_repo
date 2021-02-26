package com.virnect.data.dao;

import com.virnect.data.domain.member.MemberHistory;
import com.virnect.data.domain.roomhistory.RoomHistory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HistorySpecifications {

    public static Specification<RoomHistory> searchRoomHistory(String keyword) {
        //
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + keyword + "%");
    }

    public static Specification<RoomHistory> equalWorkspaceId(String workspaceId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("workspaceId"), workspaceId);
    }

    public static Specification<RoomHistory> joinMemberHistory(String workspaceId, String userId, List<String> userIds, String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<RoomHistory, MemberHistory> roomJoinMember = root.join("memberHistories", JoinType.LEFT);

            Predicate p1 = criteriaBuilder.and(
                    criteriaBuilder.in(roomJoinMember.get("uuid")).value(userId),
                    criteriaBuilder.equal(roomJoinMember.get("uuid"), userId));

            predicates.add(p1);

            //where
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.isNotNull(roomJoinMember.get("roomHistory")));
            predicates.add(criteriaBuilder.isFalse(roomJoinMember.get("historyDeleted")));
            predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));

            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        };
    }

    public static Specification<RoomHistory> joinMemberHistory(String workspaceId, String userId, String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<RoomHistory, MemberHistory> roomJoinMember = root.join("memberHistories", JoinType.LEFT);

            //where
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("uuid"), userId));

            predicates.add(criteriaBuilder.isNotNull(roomJoinMember.get("roomHistory")));
            predicates.add(criteriaBuilder.isFalse(roomJoinMember.get("historyDeleted")));

            predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));

            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        };
    }


    public static Specification<RoomHistory> findByTest(String workspaceId, String userId, String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            //root.fetch("", JoinType.LEFT);
            Join<RoomHistory, MemberHistory> roomJoinMember = root.join("memberHistories", JoinType.LEFT);

            if(workspaceId != null) {
                predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            }

            if(userId != null) {
                predicates.add(criteriaBuilder.equal(roomJoinMember.get("uuid"), userId));
            }

            if(keyword != null) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
            }



            return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();



            //return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            /*if(workspaceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("workspaceId"), workspaceId));
            }

            if(keyword != null) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
            }*/



            /*return query
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();*/

            //criteriaBuilder.equal(root)
        };
    }
}
