package com.googlecode.freewebdav.webdav;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import com.bradmcevoy.http.LockInfo;
import com.bradmcevoy.http.LockResult;
import com.bradmcevoy.http.LockTimeout;
import com.bradmcevoy.http.LockToken;
import com.bradmcevoy.http.LockableResource;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.http.fs.LockManager;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemcacheLockManager implements LockManager {
	private static final Logger log = Logger.getLogger(MemcacheLockManager.class.getName());
	MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

	private CurrentLock getByTokenId(String id) {
		return (CurrentLock) memcache.get( "token-" + id);
	}

	private CurrentLock getByUniqueId(String id) {
		return (CurrentLock) memcache.get( "id-" + id);
	}
	
	private void putByUniqueId(String id, CurrentLock lock) {
		memcache.put( "id-" + id, lock); 
	}
	
	private void putByTokenId(String id, CurrentLock lock) {
		memcache.put( "token-" + id, lock); 
	}

	private boolean removeByUniqueId(String id) {
		return memcache.delete( "id-" + id); 
	}
	
	private boolean removeByTokenId(String id) {
		return memcache.delete( "token-" + id); 
	}
	
	
    public LockResult lock( LockTimeout timeout, LockInfo lockInfo, LockableResource r ) {
        LockToken currentLock = currentLock( r );
        if( currentLock != null ) {
            return LockResult.failed( LockResult.FailureReason.ALREADY_LOCKED );
        }

        LockToken newToken = new LockToken( UUID.randomUUID().toString(), lockInfo, timeout );
        CurrentLock newLock = new CurrentLock( r.getUniqueId(), newToken, lockInfo.lockedByUser );
        putByUniqueId(r.getUniqueId(), newLock );
        putByTokenId(newToken.tokenId, newLock );
        return LockResult.success( newToken );
    }

    public LockResult refresh( String tokenId, LockableResource resource ) {
        CurrentLock curLock = getByTokenId( tokenId );
        curLock.token.setFrom( new Date() );
        return LockResult.success( curLock.token );
    }

    public void unlock( String tokenId, LockableResource r ) throws NotAuthorizedException {
        LockToken lockToken = currentLock( r );
        if( lockToken == null ) {
            log.finer( "not locked" );
            return;
        }
        if( lockToken.tokenId.equals( tokenId ) ) {
            removeLock( lockToken );
        } else {
            throw new NotAuthorizedException( r );
        }
    }

    private LockToken currentLock( LockableResource resource ) {
        CurrentLock curLock = getByUniqueId( resource.getUniqueId() );
        if( curLock == null ) return null;
        LockToken token = curLock.token;
        if( token.isExpired() ) {
            removeLock( token );
            return null;
        } else {
            return token;
        }
    }

    private void removeLock( LockToken token ) {
        log.finer( "removeLock: " + token.tokenId );
        CurrentLock currentLock = getByTokenId( token.tokenId );
        if( currentLock != null ) {
            removeByUniqueId( currentLock.id );
            removeByTokenId( currentLock.token.tokenId );
        } else {
            log.warning( "couldnt find lock: " + token.tokenId );
        }
    }

    public LockToken getCurrentToken( LockableResource r ) {
        CurrentLock lock = getByUniqueId( r.getUniqueId() );
        if( lock == null ) return null;
        LockToken token = new LockToken();
        token.info = new LockInfo( LockInfo.LockScope.EXCLUSIVE, LockInfo.LockType.WRITE, lock.lockedByUser, LockInfo.LockDepth.ZERO );
        token.info.lockedByUser = lock.lockedByUser;
        token.timeout = lock.token.timeout;
        token.tokenId = lock.token.tokenId;
        return token;
    }
    
    static class CurrentLock implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	final String id;
    	final LockToken token;
    	final String lockedByUser;
    	
    	public CurrentLock(String id, LockToken token, String lockedByUser) {
    		this.id = id;
    		this.token = token;
    		this.lockedByUser = lockedByUser;
    	}
    }
}
