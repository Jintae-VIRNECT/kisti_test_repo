package com.virnect.process.dao.aruco;

public interface ArucoCustomRepository {
    long getArucoId();

    boolean deallocatate(String contentUUID);
}
