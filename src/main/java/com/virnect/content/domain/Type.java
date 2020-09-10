package com.virnect.content.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@Enumerated(EnumType.STRING)
	private Types type;

	@OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
	private List<Content> contentList = new ArrayList<>();

	@OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
	private List<TypeDevice> typeDeviceList = new ArrayList<>();

	@Builder
	public Type(Types type) {
		this.type = type;
		this.contentList = new ArrayList<>();
		this.typeDeviceList = new ArrayList<>();
	}

	public void addContent(Content content) {
		content.setType(this);
		contentList.add(content);
	}

	public void addDevice(TypeDevice typeDevice) {
		typeDevice.setType(this);
		typeDeviceList.add(typeDevice);
	}
}
