package com.virnect.workspace.event.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class CacheDeleteEventHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    @EventListener(UserWorkspacesDeleteEvent.class)
    public void userWorkspacesDeleteEventListener(UserWorkspacesDeleteEvent userWorkspacesDeleteEvent) {
        Set<String> keys = redisTemplate.keys("userWorkspaces::*");
        if (!CollectionUtils.isEmpty(keys)) {
            keys.forEach(key -> {
                if (key.startsWith("userWorkspaces::".concat(userWorkspacesDeleteEvent.getUserId()))) {
                    log.info("[WORKSPACE CACHE DELETE EVENT] - [{}]", userWorkspacesDeleteEvent.toString());
                    redisTemplate.delete(key);
                }
            });
        }
    }
}
