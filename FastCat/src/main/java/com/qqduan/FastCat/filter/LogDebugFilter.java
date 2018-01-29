package com.qqduan.FastCat.filter;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.qqduan.FastCat.core.Invocation;
import com.qqduan.FastCat.core.Result;
import com.qqduan.FastCat.interfaces.IFilter;
import com.qqduan.FastCat.interfaces.IInvoker;

import io.vertx.ext.web.FileUpload;

public class LogDebugFilter implements IFilter {
	private static final Logger LOGGER = Logger.getLogger(LogDebugFilter.class);

	@Override
	public Result invoke(IInvoker invoker, Invocation invocation) {
		String mapping = invocation.getAttachment(invocation.MAPPING);
		Map<String, String> request = invocation.getAttachment(invocation.REQUEST);
		Set<FileUpload> fileUploads = invocation.getAttachment(invocation.UPLOAD_FILES);
		StringBuffer debug = new StringBuffer();
		debug.append("\n");
		debug.append(">>>>>> ");
		debug.append("mapping = ");
		debug.append(mapping);
		debug.append("\n");
		debug.append(">>>>>> ");
		debug.append("request = ");
		debug.append(request);
		debug.append("\n");
		debug.append(">>>>>> ");
		if (fileUploads != null && !fileUploads.isEmpty()) {
			debug.append("fileUploads size = ");
			debug.append(fileUploads.size());
			fileUploads.forEach(fileUpload -> {
				debug.append("\n");
				debug.append(">>>>>>>>>>>> ");
				debug.append("contentType = ");
				debug.append(fileUpload.contentType());
				debug.append(",");
				debug.append("name = ");
				debug.append(fileUpload.name());
				debug.append(",");
				debug.append("size = ");
				debug.append(fileUpload.size());
				debug.append(",");
				debug.append("file = ");
				debug.append(fileUpload.fileName());
				debug.append(",");
				debug.append("uploadedFile = ");
				debug.append(fileUpload.uploadedFileName());
			});
		} else {
			debug.append("fileUploads size = 0");
		}
		LOGGER.debug(debug.toString());
		return invoker.invoke(invocation);
	}

}
