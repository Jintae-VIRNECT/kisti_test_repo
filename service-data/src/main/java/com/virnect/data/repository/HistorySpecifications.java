package com.virnect.data.repository;

import com.virnect.data.dao.MemberHistory;
import com.virnect.data.dao.RoomHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HistorySpecifications {

    public static Specification<RoomHistory> searchRoomHistory(String keyword) {
        //
        return (Specification<RoomHistory>) ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + keyword + "%")
        );
    }

    public static Specification<RoomHistory> equalWorkspaceId(String workspaceId) {

        return (Specification<RoomHistory>) ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("workspaceId"), workspaceId)
        );
    }

    public static Specification<RoomHistory> joinMemberHistory(String workspaceId, List<String> userIds, String keyword) {
        return (Specification<RoomHistory>) ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<RoomHistory, MemberHistory> roomJoinMember = root.join("memberHistories", JoinType.LEFT);

            //where
            predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            predicates.add(criteriaBuilder.isNotNull(roomJoinMember.get("roomHistory")));
            predicates.add(criteriaBuilder.isFalse(roomJoinMember.get("historyDeleted")));

            //predicates.add(criteriaBuilder.in(roomJoinMember.get("uuid")).value(userIds));
            //predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));

            return query
                    .where(criteriaBuilder.in(roomJoinMember.get("uuid")).value(userIds))
                    .where(criteriaBuilder.or(criteriaBuilder.like(root.get("title"), "%" + keyword + "%")))
                    .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                    .distinct(true)
                    .getRestriction();
        });
    }

    public static Specification<RoomHistory> joinMemberHistory(String workspaceId, String userId, String keyword) {
        return (Specification<RoomHistory>) ((root, query, criteriaBuilder) -> {
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
        });
    }


    public static Specification<RoomHistory> findByTest(String workspaceId, String userId, String keyword) {
        return (Specification<RoomHistory>) ((root, query, criteriaBuilder) -> {
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
        });
    }
}
