package com.virnect.data.repository;

import com.virnect.data.dao.MemberHistory;
import com.virnect.data.dao.RoomHistory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

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

    public static Specification<RoomHistory> findByTest(String workspaceId, String keyword) {
        return (Specification<RoomHistory>) ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();



            Join<RoomHistory, MemberHistory> roomJoinMember = root.join("RoomHistory.room_history_id", JoinType.LEFT);
            if(workspaceId != null) {
                predicates.add(criteriaBuilder.equal(roomJoinMember.get("workspaceId"), workspaceId));
            }

            if(keyword != null) {
                predicates.add(criteriaBuilder.like(roomJoinMember.get("title"), "%" + keyword + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

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
