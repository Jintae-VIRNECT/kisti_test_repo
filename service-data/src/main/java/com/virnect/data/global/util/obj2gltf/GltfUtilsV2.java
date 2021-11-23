package com.virnect.data.global.util.obj2gltf;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.GltfException;

/**
 * Utility methods related to {@link GlTF}s
 */
class GltfUtilsV2
{
	/**
	 * Creates a deep copy of the given {@link GlTF}.<br>
	 * <br>
	 * Note: Some details about the copy are not specified. E.g. whether
	 * values that are mapped to <code>null</code> are still contained
	 * in the copy. The goal of this method is to create a copy that is,
	 * as far as reasonably possible, "structurally equivalent" to the
	 * given input.
	 *
	 * @param gltf The input
	 * @return The copy
	 * @throws GltfException If the copy can not be created
	 */
	static GlTF copy(GlTF gltf)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			objectMapper.writeValue(baos, gltf);
			return objectMapper.readValue(baos.toByteArray(), GlTF.class);
		}
		catch (IOException e)
		{
			throw new GltfException("Could not copy glTF", e);
		}
	}

	/**
	 * Creates a shallow copy of the given {@link BufferView}
	 *
	 * @param bufferView The {@link BufferView}
	 * @return The copy
	 */
	static BufferView copy(BufferView bufferView)
	{
		BufferView copy = new BufferView();
		copy.setExtensions(bufferView.getExtensions());
		copy.setExtras(bufferView.getExtras());
		copy.setName(bufferView.getName());
		copy.setBuffer(bufferView.getBuffer());
		copy.setByteOffset(bufferView.getByteOffset());
		copy.setByteLength(bufferView.getByteLength());
		copy.setTarget(bufferView.getTarget());
		copy.setByteStride(bufferView.getByteStride());
		return copy;
	}


	/**
	 * Creates a shallow copy of the given {@link Image}
	 *
	 * @param image The {@link Image}
	 * @return The copy
	 */
	static Image copy(Image image)
	{
		Image copy = new Image();
		copy.setExtensions(image.getExtensions());
		copy.setExtras(image.getExtras());
		copy.setName(image.getName());
		copy.setUri(image.getUri());
		copy.setBufferView(image.getBufferView());
		copy.setMimeType(image.getMimeType());
		return copy;
	}

	/**
	 * Private constructor to prevent instantiation
	 */
	private GltfUtilsV2()
	{
		// Private constructor to prevent instantiation
	}
}