package com.virnect.download.dao;

import com.virnect.download.domain.App;
import com.virnect.download.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-Download
 * DATE: 2020-05-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface AppRepositoryCustom {
    List<App> getAppList(Product product);

    Long getLatestVersionCodeByPackageName(String packageName);

    Optional<App> getLatestVersionAppInfoByPackageName(String packageName);

    long registerSigningKeyByPackageName(String packageName, String signingKey);

    List<App> findByPackageNameAndSignature(String packageName, String signature);

}
