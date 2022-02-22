package com.virnect.content.dao.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.QContent;
import com.virnect.content.domain.QTarget;
import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.YesOrNo;

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
	public Page<Content> getContent(
		String workspaceUUID, String userUUID, String search, String shareds, String converteds,
		List<String> userUUIDList, Pageable pageable, List<String> targetType
	) {
		QContent qContent = QContent.content;
		QTarget qTarget = QTarget.target;
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
					List<YesOrNo> convertConverted = Stream.of(arrConverted)
						.map(YesOrNo::valueOf)
						.collect(Collectors.toList());
					query = query.where(qContent.converted.in(convertConverted));
				} else {
					YesOrNo converted = YesOrNo.valueOf(converteds);
					query = query.where(qContent.converted.eq(converted));
				}
			}
		}

		if (!CollectionUtils.isEmpty(targetType) && !targetType.contains("ALL")) {
			List<TargetType> targetTypeList = targetType.stream().map(TargetType::valueOf).collect(Collectors.toList());
			query = query.join(qContent.targetList, qTarget)
				.where(qTarget.type.in(targetTypeList));
		}

		// apply pagination
		final List<Content> contentList = getQuerydsl().applyPagination(pageable, query).fetch();

		// wrap result as Page Class
		return new PageImpl<>(contentList, pageable, query.fetchCount());
	}

	public Optional<Content> getContentOfTarget(String targetData) {
		QContent qContent = QContent.content;
		QTarget qTarget = QTarget.target;
		Content content = from(qContent).join(qContent.targetList, qTarget)
			.where(qTarget.data.eq(targetData))
			.fetchOne();
		return Optional.ofNullable(content);
	}

	@Override
	public Long getWorkspaceStorageSize(String workspaceUUID) {
		QContent qContent = QContent.content;

		return from(qContent)
			.select(qContent.size.sum())
			.where(qContent.workspaceUUID.eq(workspaceUUID))
			.fetchOne();
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
	public Long calculateTotalStorageAmountByWorkspaceId(String workspaceId) {
		QContent qContent = QContent.content;
		Optional<Long> calculateTotalUsedStorageAmount = Optional.ofNullable(
			from(qContent).select(qContent.size.sum()).where(qContent.workspaceUUID.eq(workspaceId)).fetchOne()
		);
		if (calculateTotalUsedStorageAmount.isPresent()) {
			long totalStorageUsage = calculateTotalUsedStorageAmount.get();
			totalStorageUsage /= 1024 * 1024;
			return totalStorageUsage;
		}
		return 0L;
	}

	@Override
	public long deleteAllContentByWorkspaceUUID(String workspaceUUID) {
		QContent qContent = QContent.content;
		return delete(qContent).where(qContent.workspaceUUID.eq(workspaceUUID)).execute();
	}

	@Override
	public List<Content> findContentAndTargetListByWorkspaceUUID(String workspaceUUID) {
		QContent qContent = QContent.content;
		QTarget qTarget = QTarget.target;
		return from(qContent).select(qContent).where(qContent.workspaceUUID.eq(workspaceUUID))
			.join(qContent.targetList, qTarget).fetchJoin().fetch();
	}
}
