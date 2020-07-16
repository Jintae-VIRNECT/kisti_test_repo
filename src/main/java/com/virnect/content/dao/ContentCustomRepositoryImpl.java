package com.virnect.content.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.QContent;
import com.virnect.content.domain.QTarget;
import com.virnect.content.domain.YesOrNo;
import com.virnect.content.dto.response.ContentResourceUsageInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description Content Domain QueryDsl Implementation Class
 * @since 2020.03.13
 */
public class ContentCustomRepositoryImpl extends QuerydslRepositorySupport implements ContentCustomRepository {
    private static String FILTER_DELIMITER = ",";

    public ContentCustomRepositoryImpl() {
        super(Content.class);
    }

    @Override
    public Page<Content> getContent(String workspaceUUID, String userUUID, String search, String shareds, String converteds, List<String> userUUIDList, Pageable pageable) {
        QContent qContent = QContent.content;
        JPQLQuery<Content> query = from(qContent);

        // apply search keyword
        if (search != null) {
            query = query.where(qContent.name.contains(search)
                    .or(qContent.userUUID.in(userUUIDList))
                    .or(qContent.userUUID.eq(search)));
        }

        if (userUUID != null) {
            // userUUID 값이 있으면 '내 컨텐츠 목록' 조회임.
            query = query.where(qContent.userUUID.eq(userUUID));
        }

        if (workspaceUUID != null) {
            query = query.where(qContent.workspaceUUID.contains(workspaceUUID));
        }

        // 삭제된 컨텐츠 제외
        query = query.where(qContent.deleted.eq(YesOrNo.NO));

        // apply specific filter keyword
        if (!shareds.equals("ALL")) {
            String[] arrShared = shareds.split(FILTER_DELIMITER);
            if (arrShared.length > 1) {
                List<YesOrNo> convertShared = Stream.of(arrShared).map(YesOrNo::valueOf).collect(Collectors.toList());
                query = query.where(qContent.shared.in(convertShared));
            } else {
                YesOrNo shared = YesOrNo.valueOf(shareds);
                query = query.where(qContent.shared.eq(shared));
            }
        }

        if (converteds != null) {
            if (!converteds.equals("ALL")) {
                String[] arrConverted = converteds.split(FILTER_DELIMITER);
                if (arrConverted.length > 1) {
                    List<YesOrNo> convertConverted = Stream.of(arrConverted).map(YesOrNo::valueOf).collect(Collectors.toList());
                    query = query.where(qContent.converted.in(convertConverted));
                } else {
                    YesOrNo converted = YesOrNo.valueOf(converteds);
                    query = query.where(qContent.converted.eq(converted));
                }
            }
        }

        // apply pagination
        final List<Content> contentList = getQuerydsl().applyPagination(pageable, query).fetch();

        // wrap result as Page Class
        return new PageImpl<>(contentList, pageable, query.fetchCount());
    }

    public Content getContentOfTarget(String targetData) {
        QContent qContent = QContent.content;
        QTarget qTarget = QTarget.target;
        JPQLQuery<Content> query = from(qContent).join(qContent.targetList, qTarget).where(qTarget.data.eq(targetData));
        return query.fetchOne();
    }

    @Override
    public Long getWorkspaceStorageSize(String workspaceUUID) {
        QContent qContent = QContent.content;

        Long sumSize = from(qContent)
                .select(qContent.size.sum())
                .where(qContent.workspaceUUID.eq(workspaceUUID))
                .fetchOne();

        return sumSize;
    }

    @Override
    public Long getWorkspaceDownload(String workspaceUUID) {
        QContent qContent = QContent.content;

        Long sumDownload = from(qContent)
                .select(qContent.downloadHits.sum())
                .where(qContent.workspaceUUID.eq(workspaceUUID))
                .fetchOne();

        return sumDownload;
    }

    @Override
    public List<Tuple> countByUsers(String workspaceUUID, List<String> userUUIDList) {
        QContent qContent = QContent.content;

        List<Map<String, Object>> mapList = new ArrayList<>();

        List<Tuple> tupleList = from(qContent)
                .select(qContent.userUUID.as("userUUID")
                        , qContent.id.count().as("contentCount"))
                .where(qContent.workspaceUUID.eq(workspaceUUID))
                .where(qContent.userUUID.in(userUUIDList))
                .groupBy(qContent.userUUID)
                .fetch();

        return tupleList;
    }

    @Override
    public ContentResourceUsageInfoResponse calculateResourceUsageAmountByWorkspaceId(String workspaceId) {
        QContent qContent = QContent.content;
        Tuple result = from(qContent)
                .select(qContent.workspaceUUID,
                        qContent.size.sum().as("storageUsage"),
                        qContent.downloadHits.sum().as("totalHit")
                )
                .where(qContent.workspaceUUID.eq(workspaceId)).fetchOne();

        Long totalStorageUsage = result.get(qContent.size.sum().as("storageUsage"));
        Long totalHits = result.get(qContent.downloadHits.sum().as("totalHit"));

        if (totalStorageUsage == null) {
            totalStorageUsage = 0L;
        }

        if (totalHits == null) {
            totalHits = 0L;
        }

        totalStorageUsage /= 1024 * 1024;


        return new ContentResourceUsageInfoResponse(workspaceId, totalStorageUsage, totalHits, LocalDateTime.now());
    }
}
