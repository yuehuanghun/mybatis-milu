package com.yuehuanghun.mybatismilu.spring.boot.autoconfigure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ibatis.io.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

public class SpringBootVFS extends VFS {

	private static Charset urlDecodingCharset;
	private static Supplier<ClassLoader> classLoaderSupplier;
	private final ResourcePatternResolver resourceResolver;

	static {
		setUrlDecodingCharset(Charset.defaultCharset());
		setClassLoaderSupplier(ClassUtils::getDefaultClassLoader);
	}

	public SpringBootVFS() {
		this.resourceResolver = new PathMatchingResourcePatternResolver(classLoaderSupplier.get());
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	protected List<String> list(URL url, String path) throws IOException {
		String urlString = URLDecoder.decode(url.toString(), urlDecodingCharset);
		String baseUrlString = urlString.endsWith("/") ? urlString : urlString.concat("/");
		Resource[] resources = resourceResolver.getResources(baseUrlString + "**/*.class");
		return Stream.of(resources).map(resource -> preserveSubpackageName(baseUrlString, resource, path))
				.collect(Collectors.toList());
	}

	public static void setUrlDecodingCharset(Charset charset) {
		urlDecodingCharset = charset;
	}

	public static void setClassLoaderSupplier(Supplier<ClassLoader> supplier) {
		classLoaderSupplier = supplier;
	}

	private static String preserveSubpackageName(final String baseUrlString, final Resource resource,
			final String rootPath) {
		try {
			return rootPath + (rootPath.endsWith("/") ? "" : "/") + Normalizer
					.normalize(URLDecoder.decode(resource.getURL().toString(), urlDecodingCharset), Normalizer.Form.NFC)
					.substring(baseUrlString.length());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
