package com.virnect.content.dao.target;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.QTarget;
import com.virnect.content.domain.Target;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class TargetCustomRepositoryImpl extends QuerydslRepositorySupport implements TargetCustomRepository {

    public TargetCustomRepositoryImpl() {
        super(Target.class);
    }

    @Override
    public long deleteAllTargetInfoByContent(List<Content> contentList) {
        QTarget qTarget = QTarget.target;
        return delete(qTarget).where(qTarget.content.in(contentList)).execute();
    }
}
