package com.virnect.process.dto.rest.response.content;

import com.virnect.process.domain.ItemType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class MetadataInfoResponse {
    @ApiModelProperty(value = "공정 메타데이터")
    private Content contents;

    @Getter
    @Setter
    public static class Content {
        @ApiModelProperty(value = "공정(컨텐츠) 식별자", notes = "컨텐츠를 식별하기 위해 사용되는 식별자", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
        private String id;
        @ApiModelProperty(value = "arcuo 값", position = 1, example = "1")
        private int aruco;
        @ApiModelProperty(value = "공정(컨텐츠) 이름", position = 2, example = "고길동")
        private String name;
        @ApiModelProperty(value = "공정(컨텐츠) 담당자", position = 3, example = "498b1839dc29ed7bb2ee90ad6985c608")
        private String managerUUID;
        @ApiModelProperty(value = "세부 공정 수(씬그룹 갯수)", position = 4, example = "5")
        private int subProcessTotal;
        @ApiModelProperty(value = "세부 공정 정보 리스트(씬그룹 정보 리스트)", position = 5)
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
        @ApiModelProperty(value = "세부공정(씬그룹) 식별자", notes = "세부 공정(씬그룹)을 식별하기 위해 사용되는 식별자", example = "0292b07c-414a-499d-82ee-ad14e2e40dc1")
        private String id;
        @ApiModelProperty(value = "세부공정(씬그룹) 우선 순위", notes = "세부 공정(씬그룹)의 우선 순위", position = 1, example = "1")
        private int priority;
        @ApiModelProperty(value = "세부공정(씬그룹) 이름", notes = "세부 공정(씬그룹)의 이름", position = 2, example = "자재 절단")
        private String name;
        @ApiModelProperty(value = "세부작업 갯수(씬의 갯수)", notes = "세부작업의 갯수(씬의 갯수)", position = 3, example = "15")
        private int jobTotal;
        @ApiModelProperty(value = "세부작업 정보 리스트", notes = "세부 작업 정보들에 대한 배열", position = 4)
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
        @ApiModelProperty(value = "작업(씬) 식별자", notes = "작업(씬)을 구별하기 위해 사용되는 식별자", example = "0292b07c-414a-499d-82ee-ad14e2e40dc1")
        private String id;
        @ApiModelProperty(value = "작업(씬) 우선순위", notes = "작업(씬)의 우선순위", position = 1, example = "1")
        private int priority;
        @ApiModelProperty(value = "작업(씬) 명", notes = "작업(씬)의 명칭", position = 2, example = "전기톱 준비")
        private String name;
        @ApiModelProperty(value = "세부 작업(Object) 의 갯수", notes = "세부 작업(Object)의 갯수", position = 3, example = "20")
        private int subJobTotal;
        @ApiModelProperty(value = "레포트 정보 리스트", position = 4)
        private List<ReportObject> reportObjects;

        @Override
        public String toString() {
            return "Scene{" +
                    "id=" + id +
                    ", priority=" + priority +
                    ", name='" + name + '\'' +
                    ", subJobTotal=" + subJobTotal +
                    ", reportObjects=" + reportObjects +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ReportObject {
        @ApiModelProperty(value = "레포트 식별자", notes = "레포트를 식별하기 위해 사용되는 식별자", example = "b5db6bb8-9976-4865-859c-1b98e57a3dc5")
        private String id;
        @ApiModelProperty(value = "레포트 아이템 정보 리스트", notes = "레포트에 들어있는 아이템들에 대한 정보를 담은 배열", position = 1)
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
        @ApiModelProperty(value = "레포트 아이템 식별자", notes = "레포트 아이템을 구별하기 위해 사용되는 식별자", example = "b5db6bb8-9976-2231-231c-ab238e57a3dc5")
        private String id;
        @ApiModelProperty(value = "레포트 아이템 우선순위", notes = "레포트 아이템의 우선 순위", position = 1, example = "1")
        private int priority;
        @ApiModelProperty(value = "레포트 아이템 타임", notes = "레포트 아이템의 종류 입니다", position = 2, example = "REPORT")
        private ItemType type;
        @ApiModelProperty(value = "레포트 아이텝 이름", notes = "레포트 아이템의 명칭입니다.", position = 3, example = "자른 자재의 무게 기입")
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
}
