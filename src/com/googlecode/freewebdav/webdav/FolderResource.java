package com.googlecode.freewebdav.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Logger;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.io.StreamUtils;
import com.googlecode.freewebdav.entities.WebdavFile;
import com.googlecode.freewebdav.entities.WebdavFileData;
import com.googlecode.freewebdav.entities.WebdavFolder;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

@SuppressWarnings("unchecked")
public class FolderResource extends NamedCollectionResource<WebdavFolder> implements com.bradmcevoy.http.FolderResource {
	private static final Logger log = Logger.getLogger(FolderResource.class.getName());
	public static Long MAX_AGE = 60L * 60L; // 1hr
	public FolderResource(WebdavFolder wf) { super(wf); }

	protected WebdavFolder getFolder() {
		return (WebdavFolder) item;
	}
	
	@Override
	protected void ensureChildren() {
		Key<WebdavFolder> parent = ofy.getFactory().getKey(getFolder());
		for(WebdavFolder wf : ofy.query(WebdavFolder.class).filter("parent", parent))
			_children.put(wf.getName(), inject(new FolderResource(wf)));

		for(WebdavFile wf : ofy.query(WebdavFile.class).filter("parent", parent))
			_children.put(wf.getName(), inject(new FileResource(wf)));
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		ofy.delete(getKey(getFolder()));
	}

//	@Override
//	public boolean isLockedOutRecursive(Request arg0) {
//		return false;
//	}

	@Override
	public CollectionResource createCollection(String name) throws NotAuthorizedException, ConflictException, BadRequestException {
		return createFolder(ofy, getKey(getFolder()), name);
	}
	
	public static CollectionResource createFolder(Objectify ofy, Key<WebdavFolder> parent, String name) throws NotAuthorizedException, ConflictException, BadRequestException {
		WebdavFolder wf = new WebdavFolder();
		wf.setParent(parent);
		wf.setName(name);
		ofy.put(wf);
		return new FolderResource(wf);		
	} 

	@Override
	public Resource createNew(String s, InputStream is, Long length, String contentType) throws IOException, ConflictException {		
		return createFile(ofy, getKey(getFolder()), s, is, length, contentType);
	}

	public static Resource createFile(Objectify ofy, Key<WebdavFolder> parent, String s, InputStream is, Long length, String contentType) throws IOException, ConflictException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamUtils.readTo(is, bos);

		byte[] data = bos.toByteArray();
		Key<WebdavFileData> dataKey = ofy.put(new WebdavFileData(data));
		
		WebdavFile wf = new WebdavFile();
		wf.setContentType(fixCT(contentType));
		wf.setBytes(data.length);
		wf.setName(s);
		wf.setData(dataKey);
		wf.setParent(parent);
		ofy.put(wf);
		
		log.info("Saved WebdavFile: " + wf.toString());
		
		return new FileResource(wf);
	}

	/** fixes up the content-type */
	static String fixCT(String ct) {
		if(ct.indexOf(",") > 0)
			return ct.substring(0, ct.indexOf(","));
		return ct;
	}
	@Override
	public void moveTo(CollectionResource rDest, String name) throws ConflictException, NotAuthorizedException, BadRequestException {
		if (rDest instanceof FolderResource) {
			FolderResource fr = (FolderResource)rDest;
			//same parent means a rename
			if(getFolder().getParent().equals(getKey(fr.getFolder()))) {
				getFolder().setName(name);
				ofy.put(getFolder());
				return;
			}
		}
		throw new NotAuthorizedException(rDest);
	}

	@Override
	public void copyTo(CollectionResource toCollection, String name) throws NotAuthorizedException, BadRequestException, ConflictException {
		throw new BadRequestException(this, "recursive copy is not supported yet");
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException {
		throw new IOException("not sure what to send :)");
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return MAX_AGE;
	}

	@Override
	public String getContentType(String accepts) {
		return "text/directory";
	}

	@Override
	public Long getContentLength() {
		return 0L;
	}
}
