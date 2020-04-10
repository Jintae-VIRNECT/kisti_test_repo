package com.virnect.content.domain;

import com.virnect.content.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.10
 */
@Getter
@Setter
@Entity
@Audited
@Table(name = "type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Type extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long id;

    @Column(name = "type")
    private Types type;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<Content> contentList = new ArrayList<>();

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<TypeDevice> typeDeviceList = new ArrayList<>();

    public void addContent(Content content) {
        content.setType(this);
        contentList.add(content);
    }

    public void addDevice(TypeDevice typeDevice) {
        typeDevice.setType(this);
        typeDeviceList.add(typeDevice);
    }

    @Builder
    public Type(Types type) {
        this.type = type;
        this.contentList = new ArrayList<>();
        this.typeDeviceList = new ArrayList<>();
    }
}
