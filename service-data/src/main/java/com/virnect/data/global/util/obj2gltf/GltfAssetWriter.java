package com.virnect.data.global.util.obj2gltf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDate;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfWriter;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.io.v1.GltfAssetWriterV1;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.io.v2.GltfAssetWriterV2;

public class GltfAssetWriter {

	public GltfAssetWriter()
	{
		// Default constructor
	}

	/**
	 * Write the the given {@link GltfAsset} to the given file.
	 * The {@link GltfAsset#getBinaryData() binary data} will be ignored.
	 * The {@link GltfAsset#getReferenceDatas() reference data elements}
	 * will be written to files that are determined by resolving the
	 * (relative) URLs of the references against the parent of the specified
	 * file.
	 *
	 * @param gltfAsset The {@link GltfAsset}
	 * @param gltfFile The file for the JSON part
	 * @throws IOException If an IO error occurred
	 */
	public ObjectToGltfFile write(GltfAsset gltfAsset, File gltfFile, File binFile)
		throws IOException
	{

		FileOutputStream outputStream = new FileOutputStream(gltfFile);
		writeJson(gltfAsset, outputStream);
		outputStream.close();

		for (Map.Entry<String, ByteBuffer> entry :
			gltfAsset.getReferenceDatas().entrySet())
		{
			ByteBuffer data = entry.getValue();
			try (@SuppressWarnings("resource")
				 WritableByteChannel writableByteChannel =
					 Channels.newChannel(new FileOutputStream(binFile)))
			{
				writableByteChannel.write(data.slice());
				writableByteChannel.close();
			}
		}
		return ObjectToGltfFile.builder()
			.binFile(binFile)
			.gltfFile(gltfFile)
			.build();
	}

	public File write(GltfAsset gltfAsset)
		throws IOException
	{
		String objectName = String.format("%s_%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20));
		File gltfFile = new File(objectName +".gltf");
		FileOutputStream outputStream = new FileOutputStream(gltfFile);
		writeJson(gltfAsset, outputStream);
		outputStream.close();
		return gltfFile;
	}

	/**
	 * Write the JSON part of the given {@link GltfAsset} to a file with
	 * the given name. The {@link GltfAsset#getBinaryData() binary data}
	 * and {@link GltfAsset#getReferenceDatas() reference data elements}
	 * will be ignored.
	 *
	 * @param gltfAsset The {@link GltfAsset}
	 * @param fileName The file name for the JSON file
	 * @throws IOException If an IO error occurred
	 */
	public void writeJson(GltfAsset gltfAsset, String fileName)
		throws IOException
	{
		writeJson(gltfAsset, new File(fileName));
	}

	/**
	 * Write the JSON part of the given {@link GltfAsset} to a file with
	 * the given name. The {@link GltfAsset#getBinaryData() binary data}
	 * and {@link GltfAsset#getReferenceDatas() reference data elements}
	 * will be ignored.
	 *
	 * @param gltfAsset The {@link GltfAsset}
	 * @param file The file for the JSON part
	 * @throws IOException If an IO error occurred
	 */
	public void writeJson(GltfAsset gltfAsset, File file)
		throws IOException
	{
		try (OutputStream outputStream = new FileOutputStream(file))
		{
			writeJson(gltfAsset, outputStream);
		}
	}

	/**
	 * Write the JSON part of the given {@link GltfAsset} to the given
	 * output stream. The {@link GltfAsset#getBinaryData() binary data}
	 * and {@link GltfAsset#getReferenceDatas() reference data elements}
	 * will be ignored. The caller is responsible for closing the given
	 * stream.
	 *
	 * @param gltfAsset The {@link GltfAsset}
	 * @param outputStream The output stream
	 * @throws IOException If an IO error occurred
	 */
	public void writeJson(GltfAsset gltfAsset, OutputStream outputStream)
		throws IOException
	{
		Object gltf = gltfAsset.getGltf();
		GltfWriter gltfWriter = new GltfWriter();
		gltfWriter.write(gltf, outputStream);
	}

	/**
	 * Write the given {@link GltfAsset} as a binary glTF asset to file with
	 * the given name.
	 *
	 * @param gltfAsset The {@link GltfAsset}
	 * @param fileName The file name for the JSON file
	 * @throws IOException If an IO error occurred
	 */
	public void writeBinary(GltfAsset gltfAsset, String fileName)
		throws IOException
	{
		writeBinary(gltfAsset, new File(fileName));
	}

	/**
	 * Write the given {@link GltfAsset} as a binary glTF asset to the given
	 * file
	 *
	 * @param gltfAsset The {@link GltfAsset}
	 * @param file The file
	 * @throws IOException If an IO error occurred
	 */
	public void writeBinary(GltfAsset gltfAsset, File file)
		throws IOException
	{
		try (OutputStream outputStream = new FileOutputStream(file))
		{
			writeBinary(gltfAsset, outputStream);
		}
	}

	/**
	 * Write the given {@link GltfAsset} as a binary glTF asset to the
	 * given output stream. The caller is responsible for closing the
	 * given stream.
	 *
	 * @param gltfAsset The {@link GltfAsset}
	 * @param outputStream The output stream
	 * @throws IOException If an IO error occurred
	 */
	public void writeBinary(GltfAsset gltfAsset, OutputStream outputStream)
		throws IOException
	{
		if (gltfAsset instanceof GltfAssetV1)
		{
			GltfAssetV1 gltfAssetV1 = (GltfAssetV1)gltfAsset;
			GltfAssetWriterV1 gltfAssetWriterV1 = new GltfAssetWriterV1();
			gltfAssetWriterV1.writeBinary(gltfAssetV1, outputStream);
		}
		else if (gltfAsset instanceof de.javagl.jgltf.model.io.v2.GltfAssetV2)
		{
			de.javagl.jgltf.model.io.v2.GltfAssetV2 gltfAssetV2 = (GltfAssetV2)gltfAsset;
			GltfAssetWriterV2 gltfAssetWriterV2 = new GltfAssetWriterV2();
			gltfAssetWriterV2.writeBinary(gltfAssetV2, outputStream);
		}
		else
		{
			throw new IOException(
				"The gltfAsset has an unknown version: " + gltfAsset);
		}
	}


}
