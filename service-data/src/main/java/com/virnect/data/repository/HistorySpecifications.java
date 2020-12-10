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
            if(workspaceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("workspaceId"), workspaceId));
            }

            if(keyword != null) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
            }

            Join<RoomHistory, MemberHistory> roomJoinMember = root.join("room_history_id");

            Path


            return query
                    .where(criteriaBuilder.and(predicates.toArray()))
                    .distinct(true)
                    .getRestriction();

            //criteriaBuilder.equal(root)
        });
    }
}
