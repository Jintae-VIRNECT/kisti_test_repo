package com.virnect.process.dto.rest.response.content;

import com.virnect.process.domain.ItemType;
import lombok.Getter;
import lombok.Setter;

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
        private List<Scene> scenes;

        @Override
        public String toString() {
            return "SceneGroup{" +
                    "id='" + id + '\'' +
                    ", priority=" + priority +
                    ", name='" + name + '\'' +
                    ", jobTotal=" + jobTotal +
                    ", sceneList=" + scenes +
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
        private ItemType type;
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
        private String jobId;
        private String normalTorque;
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
