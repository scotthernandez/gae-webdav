package com.googlecode.freewebdav.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.DeletableCollectionResource;
import com.bradmcevoy.http.MakeCollectionableResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.io.StreamUtils;
import com.googlecode.freewebdav.entities.WebdavFile;
import com.googlecode.freewebdav.entities.WebdavFolder;

@SuppressWarnings("unchecked")
public class FolderResource extends NamedCollectionResource<WebdavFolder> implements PutableResource, MakeCollectionableResource, DeletableCollectionResource {
	public FolderResource(WebdavFolder wf) {super(wf, null, wf.getName());}

	@Override
	protected void ensureChildren() {
		for(WebdavFolder wf : ofy.query(WebdavFolder.class).ancestor(ofy.getFactory().getKey(_obj)))
			_children.put(wf.getName(), inject(new FolderResource(wf)));

		for(WebdavFile wf : ofy.query(WebdavFile.class).ancestor(ofy.getFactory().getKey(_obj)))
			_children.put(wf.getName(), inject(new FileResource(wf)));
		
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		ofy.delete(_obj);
	}

	@Override
	public boolean isLockedOutRecursive(Request arg0) {
		return true;
	}

	@Override
	public CollectionResource createCollection(String name) throws NotAuthorizedException, ConflictException, BadRequestException {
		WebdavFolder wf = new WebdavFolder();
		wf.setParent(getKey(_obj));
		wf.setName(name);
		ofy.put(wf);
		return new FolderResource(wf);
	}

	@Override
	public Resource createNew(String s, InputStream is, Long length, String contentType) throws IOException, ConflictException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamUtils.readTo(is, bos);
		WebdavFile wf = new WebdavFile();
		wf.setContentType(contentType);
		wf.setName(s);
		wf.setData(bos.toByteArray());
		wf.setParent(getKey(_obj));
		ofy.put(wf);
		
		return new FileResource(wf);
	}
}
