package aparmar.nai.utils;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ZipArchiveWrapper {
	private final byte[] compressedBytes;
	private final ZipArchiveEntry[] entries;
	
	public ZipArchiveWrapper(byte[] compressedBytes) throws IOException {
		this.compressedBytes = compressedBytes;
		
		ArrayList<ZipArchiveEntry> entryList = new ArrayList<>();
	    try (ZipArchiveInputStream zi = new ZipArchiveInputStream(new ByteArrayInputStream(compressedBytes))) {
	    	ZipArchiveEntry nextEntry = null;
	    	while((nextEntry = zi.getNextEntry()) != null) {
	    		entryList.add(nextEntry);
	    	}
	    }
	    entries = entryList.toArray(new ZipArchiveEntry[0]);
	}
	
	public int getEntryCount() { return entries.length; }
	public ZipArchiveEntry getEntry(int idx) { return entries[idx]; }
	
	public byte[] getEntryBytes(int idx) throws IOException {
		ZipArchiveEntry entry = entries[idx];
		byte[] decompressedEntryData = new byte[(int) entry.getSize()];
		
	    try (ZipArchiveInputStream zi = new ZipArchiveInputStream(new ByteArrayInputStream(compressedBytes))) {
	    	for (int i=0;i<=idx;i++) {
	    		zi.getNextEntry();
	    	}
	    	
    		int readBytes = IOUtils.readFully(zi, decompressedEntryData);
    		if (readBytes < decompressedEntryData.length) {
    			throw new EOFException("Insufficient bytes available for entry!?");
    		}
	    }
	    
	    return decompressedEntryData;
	}
}
