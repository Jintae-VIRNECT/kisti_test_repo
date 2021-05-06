package com.virnect.process.dto.rest.response.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.process.domain.TargetType;

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

	@Override
	public String toString() {
		return "ContentTargetResponse{" +
			"id=" + id +
			", type=" + type +
			", data='" + data + '\'' +
			", imgPath='" + imgPath + '\'' +
			", size=" + size +
			'}';
	}
}
