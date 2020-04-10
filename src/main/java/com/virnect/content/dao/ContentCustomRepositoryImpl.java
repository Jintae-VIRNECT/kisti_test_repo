package com.virnect.content.dao;

import com.querydsl.jpa.JPQLQuery;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.QContent;
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
    public Page<Content> getContent(String search, String filter, List<String> userUUDList, Pageable pageable) {
        QContent qContent = QContent.content;
        JPQLQuery<Content> query = from(qContent);

        // apply search keyword
        if (search != null) {
            query = query.where(qContent.name.contains(search).or(qContent.userUUID.in(userUUDList)));
        }

        // apply specific filter keyword
        if (!filter.equals("ALL")) {
            String[] filters = filter.split(FILTER_DELIMITER);
//            if (filters.length > 1) {
//                List<ContentStatus> convertStatus = Stream.of(filters).map(ContentStatus::valueOf).collect(Collectors.toList());
//                query = query.where(qContent.status.in(convertStatus));
//            } else {
//                ContentStatus status = ContentStatus.valueOf(filter);
//                query = query.where(qContent.status.eq(status));
//            }
        }

        // apply pagination
        final List<Content> contentList = getQuerydsl().applyPagination(pageable, query).fetch();

        // wrap result as Page Class
        return new PageImpl<>(contentList, pageable, query.fetchCount());
    }
}
