package com.virnect.data.global.util.obj2gltf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {

	private ByteBuffer mBuffer;
	public ByteBufferInputStream( ByteBuffer buffer ) {
		mBuffer = buffer;
	}
	public void close() {
		mBuffer = null;
	}

	private void ensureStreamAvailable() throws IOException {
		if (this.mBuffer == null) {
			throw new IOException("read on a closed InputStream!");
		}
	}

	@Override public int read() throws IOException {
		this.ensureStreamAvailable();
		return mBuffer.hasRemaining() ? mBuffer.get() & 0xFF : -1;
	}

	@Override public int read( byte[] buffer) throws IOException {
		return this.read(buffer, 0, buffer.length);
	}

	@Override public int read(byte[] buffer, int offset, int length) throws IOException {
		this.ensureStreamAvailable();
		if ( !( offset >= 0 && length >= 0 && length <= buffer.length - offset ) ) {
			throw new IndexOutOfBoundsException();
		} if (length == 0) {
			return 0;
		}

		int remainingSize = Math.min( mBuffer.remaining(), length);
		if (remainingSize == 0) { return -1; }

		mBuffer.get(buffer, offset, remainingSize); return remainingSize;
	}

	public long skip(long n) throws IOException {
		this.ensureStreamAvailable();
		if (n <= 0L) {
			return 0L;
		}

		int length = (int) n;
		int remainingSize = Math.min( mBuffer.remaining(), length );
		mBuffer.position(mBuffer.position() + remainingSize);
		return (long)length;
	}

	public int available() throws IOException { this.ensureStreamAvailable(); return mBuffer.remaining(); }

}
