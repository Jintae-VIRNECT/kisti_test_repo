package com.virnect.process.dto.rest.response.content;

import com.virnect.process.domain.ItemType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-23
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Contents Server Rest Request Data Transfer Class
 */
@Getter
@Setter
public class ContentRestDto {
    private Content contents;

    @Getter
    @Setter
    @ToString
    public static class Content {
        private String id;
        private String name;
        private String uuid;
        private String managerUUID;
        private int subProcessTotal;
        private List<SceneGroup> sceneGroups;
    }

    @Getter
    @Setter
    @ToString
    public static class SceneGroup {
        private String id;
        private int priority;
        private String name;
        private int jobTotal;
        private List<Scene> scenes;
    }

    @Getter
    @Setter
    @ToString
    public static class Scene {
        private String id;
        private int priority;
        private String name;
        private int subJobTotal;
        private List<ReportObject> reportObjects;
    }

    @Getter
    @Setter
    @ToString
    public static class ReportObject {
        private String id;
        private List<ReportObjectItem> items;
    }

    @Getter
    @Setter
    @ToString
    public static class ReportObjectItem {
        private String id;
        private int priority;
        private ItemType type;
        private String title;
    }
}
