package com.virnect.content.dto.response;

import com.virnect.content.domain.TargetType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentTargetResponse {
    private Long id;
    private TargetType type;
    private String data;
    private String imgPath;
    private Float size;

    @Builder
    public ContentTargetResponse(Long id, TargetType type, String data, String imgPath, Float size) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.imgPath = imgPath;
        this.size = size;
    }

    @Override
    public String toString() {
        return "ContentTargetResponse{" +
                "id=" + id +
                ", type=" + type +
                ", data='" + data + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
