package com.virnect.data.global.util.obj2gltf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.GltfModels;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfWriter;
import de.javagl.jgltf.obj.BufferStrategy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConvertObjToGltf {

	public GltfAsset createGltfAsset(
		BufferStrategy bufferStrategy,
		File originalFile
	) throws IOException {

		ObjGltfAssetCreatorV2 gltfAssetCreator = new ObjGltfAssetCreatorV2(bufferStrategy);
		gltfAssetCreator.setIndicesComponentType(GltfConstants.GL_UNSIGNED_SHORT);

		GltfAsset gltfAsset = gltfAssetCreator.create(originalFile);
		GltfModel gltfModel = GltfModels.create(gltfAsset);
		com.virnect.data.global.util.obj2gltf.GltfModelWriter gltfModelWriter = new GltfModelWriter();

		return gltfModelWriter.writeGltfAsset(gltfModel);
	}

	public File extractGltf(GltfAsset gltfAsset, String objectName) throws IOException {
		File gltfFile = new File(objectName +".gltf");
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(gltfFile);
			writeJson(gltfAsset, outputStream);
			outputStream.flush();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
		return gltfFile;
	}

	public File extractBin(GltfAsset gltfAsset) throws IOException {
		File binFile = new File("buffer0.bin");
		for (Map.Entry<String, ByteBuffer> entry :
			gltfAsset.getReferenceDatas().entrySet())
		{
			ByteBuffer data = entry.getValue();
			WritableByteChannel writableByteChannel = Channels.newChannel(new FileOutputStream(binFile));
			{
				writableByteChannel.write(data.slice());
				writableByteChannel.close();
			}
		}
		return binFile;
	}

	public void writeJson(GltfAsset gltfAsset, OutputStream outputStream)
		throws IOException
	{
		Object gltf = gltfAsset.getGltf();
		GltfWriter gltfWriter = new GltfWriter();
		gltfWriter.write(gltf, outputStream);
	}

}