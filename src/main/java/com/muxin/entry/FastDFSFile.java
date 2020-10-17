package com.muxin.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FastDFSFile {
	private String name;
	
	private byte[] content;
	
	private String ext;
	
	private String md5;
	
	private String author;
	
	public FastDFSFile(String name, byte[] content, String ext) {
		this.name = name;
		this.content = content;
		this.ext = ext;
	}
}
