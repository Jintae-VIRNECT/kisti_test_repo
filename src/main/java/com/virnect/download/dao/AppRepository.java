package com.virnect.download.dao;

import com.virnect.download.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: PF-Download
 * DATE: 2020-04-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface AppRepository extends JpaRepository<App,Long> {
    App findByUrlEquals(String url);
    App findFirstByProduct_NameOrderByCreatedDateDesc(String productName);

}
