package com.virnect.content.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MetadataDto {
    private Content contents;

    @Getter
    @Setter
    public static class Content {
        private String id;
        private int aruco;
        private String name;
        private String managerUUID;
        private int subProcessTotal;
        private List<SceneGroup> sceneGroups;

        @Override
        public String toString() {
            return "Content{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", managerUUID='" + managerUUID + '\'' +
                    ", subProcessTotal=" + subProcessTotal +
                    ", sceneGroups=" + sceneGroups +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SceneGroup {
        private String id;
        private int priority;
        private String name;
        private int jobTotal;
        private List<Scene> sceneList;

        @Override
        public String toString() {
            return "SceneGroup{" +
                    "id='" + id + '\'' +
                    ", priority=" + priority +
                    ", name='" + name + '\'' +
                    ", jobTotal=" + jobTotal +
                    ", sceneList=" + sceneList +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class Scene {
        private String id;
        private int priority;
        private String name;
        private int subJobTotal;
        private List<ReportObject> reportObjects;
        private List<SmartToolObject> smartToolObjects;

        @Override
        public String toString() {
            return "Scene{" +
                    "id=" + id +
                    ", priority=" + priority +
                    ", name='" + name + '\'' +
                    ", subJobTotal=" + subJobTotal +
                    ", reportObjects=" + reportObjects +
                    ", smartToolObjects=" + smartToolObjects +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ReportObject {
        private String id;
        private List<ReportObjectItem> items;

        @Override
        public String toString() {
            return "ReportObject{" +
                    "id=" + id +
                    ", items=" + items +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ReportObjectItem {
        private String id;
        private int priority;
        private String type;
        private String title;

        @Override
        public String toString() {
            return "ReportObjectItem{" +
                    "id=" + id +
                    ", priority=" + priority +
                    ", type=" + type +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SmartToolObject {
        private String id;
        private long jobId;
        private int normalTorque;
        private List<SmartToolObjectItem> items;

        @Override
        public String toString() {
            return "SmartToolObject{" +
                    "id=" + id +
                    ", jobId=" + jobId +
                    ", normalTorque=" + normalTorque +
                    ", items=" + items +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SmartToolObjectItem {
        private String id;
        private int batchCount;

        @Override
        public String toString() {
            return "SmartToolObjectItem{" +
                    "id=" + id +
                    ", batchCount=" + batchCount +
                    '}';
        }
    }
}

