package com.virnect.content.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.virnect.content.domain.ItemType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-04-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ToString
public class PropertiesInfo {
    private String id; //TargetId
    private float targetSize; //TargetSize
    private int subProcessTotal;
    private List<SceneGroup> sceneGroups; //PropertyInfo

    @Getter
    @Setter
    @ToString
    public static class SceneGroup {
        private String id;
        private String name;
        private int priority;
        private int jobTotal;
        private List<Scene> scenes;
    }

    @Getter
    @Setter
    @ToString
    public static class Scene {
        private String id;
        private String name;
        private int priority;
        private int subJobTotal;
        private List<ReportObject> reportObjects;
        // private List<String> smartToolObjects;
    }

    @Getter
    @Setter
    @ToString
    public static class ReportObject {
        private String id;
        private List<Item> items;
    }

    @Getter
    @Setter
    @ToString
    public static class Item {
        private String id;
        private String title;
        private int priority;
        private ItemType type;
    }
}
