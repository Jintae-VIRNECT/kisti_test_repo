package com.virnect.data.global.util.obj2gltf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.v1.GltfModelWriterV1;
import de.javagl.jgltf.model.v1.GltfModelV1;
import de.javagl.jgltf.model.v2.GltfModelV2;

public class GltfModelWriter
{
	/**
	 * Default constructor
	 */
	public GltfModelWriter()
	{
		// Default constructor
	}

	/**
	 * Write the given {@link GltfModel} to the given file. External
	 * references of buffers or images that are given via the respective
	 * URI string will be resolved against the parent directory of the
	 * given file, and the corresponding data will be written into the
	 * corresponding files.
	 *
	 * @param gltfModel The {@link GltfModel}
	 * @param gltfFile The file
	 * @throws IOException If an IO error occurs
	 */
	public ObjectToGltfFile write(
		GltfModel gltfModel,
		File gltfFile,
		File binFile
	) throws IOException {
		GltfModelV2 gltfModelV2 = (GltfModelV2)gltfModel;
		GltfModelWriterV2 gltfModelWriterV2 = new GltfModelWriterV2();
		return gltfModelWriterV2.write(gltfModelV2, gltfFile, binFile);
	}

	public File writeGltfFile(
		GltfModel gltfModel
	) throws IOException {
		GltfModelV2 gltfModelV2 = (GltfModelV2)gltfModel;
		GltfModelWriterV2 gltfModelWriterV2 = new GltfModelWriterV2();
		return gltfModelWriterV2.writeGltfFile(gltfModelV2);
	}

	public GltfAsset writeGltfAsset(
		GltfModel gltfModel
	) throws IOException {
		GltfModelV2 gltfModelV2 = (GltfModelV2)gltfModel;
		GltfModelWriterV2 gltfModelWriterV2 = new GltfModelWriterV2();
		return gltfModelWriterV2.writeGltfAsset(gltfModelV2);
	}

	/**
	 * Write the given {@link GltfModel} as a binary glTF asset to the
	 * given file
	 *
	 * @param gltfModel The {@link GltfModel}
	 * @param file The file
	 * @throws IOException If an IO error occurs
	 */
	public void writeBinary(GltfModel gltfModel, File file)
		throws IOException
	{
		try (OutputStream outputStream = new FileOutputStream(file))
		{
			writeBinary(gltfModel, outputStream);
		}
	}

	/**
	 * Write the given {@link GltfModel} as a binary glTF asset to the
	 * given output stream. The caller is responsible for closing the
	 * given stream.
	 *
	 * @param gltfModel The {@link GltfModel}
	 * @param outputStream The output stream
	 * @throws IOException If an IO error occurs
	 */
	public void writeBinary(GltfModel gltfModel, OutputStream outputStream)
		throws IOException
	{
		if (gltfModel instanceof GltfModelV1)
		{
			GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
			GltfModelWriterV1 gltfModelWriterV1 =
				new GltfModelWriterV1();
			gltfModelWriterV1.writeBinary(gltfModelV1, outputStream);
		}
		else if (gltfModel instanceof GltfModelV2)
		{
			GltfModelV2 gltfModelV2 = (GltfModelV2)gltfModel;
			de.javagl.jgltf.model.io.v2.GltfModelWriterV2 gltfModelWriterV2 =
				new de.javagl.jgltf.model.io.v2.GltfModelWriterV2();
			gltfModelWriterV2.writeBinary(gltfModelV2, outputStream);
		}
		else
		{
			throw new IOException("Unsupported glTF version: " + gltfModel);
		}
	}
}

