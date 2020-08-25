package com.virnect.content.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-08-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ToString
public class MetadataInfo {

    private Contents contents;

    @Getter
    @Setter
    @ToString
    public static class Contents {
        private String id;
        private Float targetSize;
        private String name;
        private String managerUUID;
        private int subProcessTotal;
        private List<Scenegroup> sceneGroups;
    }

    @Getter
    @Setter
    @ToString
    public static class Scenegroup {
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
        private List<Reportobject> reportObjects;
       // private List<String> smartToolObjects;
    }

    @Getter
    @Setter
    @ToString
    public static class Reportobject {
        private String id;
        private List<Item> items;
    }

    @Getter
    @Setter
    @ToString
    public static class Item {
        private String id;
        private int priority;
        private String title;
        private String item;
    }
}
