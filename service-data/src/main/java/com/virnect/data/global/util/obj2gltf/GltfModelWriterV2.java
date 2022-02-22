package com.virnect.data.global.util.obj2gltf;

import java.io.File;
import java.io.IOException;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.v1.GltfModelWriterV1;
import de.javagl.jgltf.model.v1.GltfModelV1;
import de.javagl.jgltf.model.v2.GltfModelV2;

public class GltfModelWriterV2 {

	public void write(GltfModel gltfModel, File file)
		throws IOException
	{
		if (gltfModel instanceof GltfModelV1)
		{
			GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
			GltfModelWriterV1 gltfModelWriterV1 =
				new GltfModelWriterV1();
			gltfModelWriterV1.write(gltfModelV1, file);
		}
		else if (gltfModel instanceof GltfModelV2)
		{
			GltfModelV2 gltfModelV2 = (GltfModelV2)gltfModel;
			de.javagl.jgltf.model.io.v2.GltfModelWriterV2 gltfModelWriterV2 =
				new de.javagl.jgltf.model.io.v2.GltfModelWriterV2();
			gltfModelWriterV2.write(gltfModelV2, file);
		}
		else
		{
			throw new IOException("Unsupported glTF version: " + gltfModel);
		}
	}

	public ObjectToGltfFile write(GltfModelV2 gltfModel, File gltfFile, File binFile)
		throws IOException
	{
		DefaultAssetCreatorV2 assetCreator = new DefaultAssetCreatorV2();
		GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
		com.virnect.data.global.util.obj2gltf.GltfAssetWriter gltfAssetWriter = new GltfAssetWriter();
		return gltfAssetWriter.write(gltfAsset, gltfFile, binFile);
	}

	public File writeGltfFile(GltfModelV2 gltfModel)
		throws IOException
	{
		DefaultAssetCreatorV2 assetCreator = new DefaultAssetCreatorV2();
		GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
		com.virnect.data.global.util.obj2gltf.GltfAssetWriter gltfAssetWriter = new GltfAssetWriter();
		return gltfAssetWriter.write(gltfAsset);
	}

	public GltfAsset writeGltfAsset(GltfModelV2 gltfModel) {
		DefaultAssetCreatorV2 assetCreator = new DefaultAssetCreatorV2();
		return assetCreator.create(gltfModel);
	}

}
