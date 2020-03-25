package com.virnect.process.dao.aruco;

import com.virnect.process.domain.Aruco;
import com.virnect.process.domain.QAruco;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ArucoCustomRepositoryImpl extends QuerydslRepositorySupport implements ArucoCustomRepository {
    public ArucoCustomRepositoryImpl() {
        super(Aruco.class);
    }

    @Override
    public long getArucoId() {
        QAruco qAruco = QAruco.aruco;
        return from(qAruco)
                .where(qAruco.contentUUID.isNull())
                .orderBy(qAruco.id.asc())
                .fetchFirst().getId();
    }

    @Override
    public boolean deallocatate(String contentUUID) {
        QAruco qAruco = QAruco.aruco;
        long affectRows = update(qAruco).where(qAruco.contentUUID.eq(contentUUID)).setNull(qAruco.contentUUID).execute();
        return affectRows > 0;
    }
}
