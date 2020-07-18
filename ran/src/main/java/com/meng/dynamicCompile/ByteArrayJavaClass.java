package com.meng.dynamicCompile;

import javax.tools.*;
import java.net.*;
import java.io.*;

public class ByteArrayJavaClass extends SimpleJavaFileObject {
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public ByteArrayJavaClass(String name) {
		super(URI.create("bytes:///" + name), Kind.CLASS);
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return stream;
	}

	public byte[] getBytes() {
		return stream.toByteArray();
	}
}
