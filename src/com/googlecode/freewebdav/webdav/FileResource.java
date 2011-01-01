package com.googlecode.freewebdav.webdav;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.googlecode.freewebdav.entities.WebdavFile;

public class FileResource extends AuthenticatedResource implements GetableResource, DeletableResource {
	WebdavFile file;
	protected FileResource(WebdavFile wf) { 
		file = wf;
		name = wf.getName();
	}
	@Override
	public Long getContentLength() {
		return (long) file.getData().length;
	}
	@Override
	public String getContentType(String accepts) {
		return file.getContentType();
	}
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}
	
	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException {
		writeData(file.getData(), out, range);
	}

	private void writeData(byte[] data, OutputStream out, Range range) throws IOException {
		if (range != null && range.getFinish() <= data.length && (range.getFinish()-range.getStart()) < data.length)
			out.write(data, Integer.parseInt(""+range.getStart()), Integer.parseInt(""+(range.getFinish() - range.getStart())));
		else
			out.write(data);
	}
	
	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		ofy.delete(file);
	}

}
