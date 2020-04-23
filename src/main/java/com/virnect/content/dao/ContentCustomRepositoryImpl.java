package com.virnect.content.dao;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.QContent;
import com.virnect.content.domain.QTarget;
import com.virnect.content.domain.YesOrNo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
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
    public Page<Content> getContent(String workspaceUUID, String userUUID, String search, String shareds, List<String> userUUDList, Pageable pageable) {
        QContent qContent = QContent.content;
        JPQLQuery<Content> query = from(qContent);

        // apply search keyword
        if (search != null) {
            query = query.where(qContent.name.contains(search).or(qContent.userUUID.in(userUUDList)));
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

        // apply pagination
        final List<Content> contentList = getQuerydsl().applyPagination(pageable, query).fetch();

        // wrap result as Page Class
        return new PageImpl<>(contentList, pageable, query.fetchCount());
    }

    public Content getContentOfTarget(String targetData) {
        QContent qContent = QContent.content;
        QTarget qTarget = QTarget.target;
        JPQLQuery<Content> query = from(qContent).join(qTarget).fetchJoin().where(qTarget.data.eq(targetData));
        return query.fetchOne();
    }
}
