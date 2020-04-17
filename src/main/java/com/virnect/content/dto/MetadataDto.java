package com.virnect.content.dto;

import com.virnect.content.domain.ItemType;
import com.virnect.content.domain.Target;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
public class MetadataDto {
    private Content contents;

    @Getter
    @Setter
    @ToString
    public static class Content {
        private String id;
        private String name;
        private String managerUUID;
        private int subProcessTotal;
        private List<Target> targets;
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
        private List<Scene> sceneList;
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
        private List<SmartToolObject> smartToolObjects;
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

    @Getter
    @Setter
    @ToString
    public static class SmartToolObject {
        private String id;
        private long jobId;
        private String normalTorque;
        private List<SmartToolObjectItem> items;
    }

    @Getter
    @Setter
    @ToString
    public static class SmartToolObjectItem {
        private String id;
        private int batchCount;
    }
}

