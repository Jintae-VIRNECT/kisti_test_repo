package com.virnect.data.global.util.obj2gltf;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectToGltfFile {

	private File gltfFile;
	private File binFile;

	@Builder
	public ObjectToGltfFile(
		File gltfFile, File binFile
	) {
		this.gltfFile = gltfFile;
		this.binFile = binFile;
	}
}
