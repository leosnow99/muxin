package com.muxin.utils;

import com.muxin.entry.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastDFSClient {
	static {
		try {
			String filePath = new ClassPathResource("fdfs_client.conf").getPath();
			ClientGlobal.init(filePath);
		} catch (IOException | MyException e) {
			e.printStackTrace();
		}
	}
	
	public static String[] upload(FastDFSFile file) {
		//获取文件作者
		NameValuePair[] metaList = new NameValuePair[1];
		metaList[0] = new NameValuePair(file.getName());
		
		/*
		 文件上传后的返回值
		 uploadResults[0]:文件上传所存储的组名，例如:group1
		 uploadResults[1]:文件存储路径,例如：M00/00/00/wKjThF0DBzaAP23MAAXz2mMp9oM26.jpeg
		 */
		String[] uploadResults = null;
		try {
			//创建TrackerClient客户端对象
			StorageClient storageClient = getStorageClient();
			//执行文件上传
			uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), metaList);
		} catch (IOException | MyException e) {
			e.printStackTrace();
		}
		return uploadResults;
	}
	
	public static FileInfo getFile(String groupName, String remoteFileName) {
		try {
			//创建TrackerClient对象
			StorageClient storageClient = getStorageClient();
			//获取文件信息
			return storageClient.get_file_info(groupName,remoteFileName);
		} catch (IOException | MyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream downFile(String groupName, String remoteFileName) {
		try {
			//创建TrackerClient对象
			StorageClient storageClient = getStorageClient();
			//通过StorageClient下载文件
			byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
			//将字节数组转换成字节输入流
			return new ByteArrayInputStream(fileByte);
		} catch (IOException | MyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void deleteFile(String groupName,String remoteFileName){
		try {
			StorageClient storageClient = getStorageClient();
			//通过StorageClient删除文件
			storageClient.delete_file(groupName,remoteFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static StorageClient getStorageClient() throws IOException {
		//创建TrackerClient对象
		TrackerServer trackerServer = getTrackerServer();
		//通过TrackerServer创建StorageClient
		return new StorageClient(trackerServer, null);
	}
	
	//获取组信息
	public static StorageServer getStorage(String groupName) {
		try {
			//创建TrackerClient对象
			TrackerClient trackerClient = new TrackerClient();
			//通过TrackerClient获取TrackerServer对象
			TrackerServer trackerServer = trackerClient.getConnection();
			//通过trackerClient获取Storage组信息
			return trackerClient.getStoreStorage(trackerServer, groupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//根据文件组名和文件存储路径获取Storage服务的IP、端口信息
	public static ServerInfo[] getServerInfo(String groupName, String remoteFileName){
		try {
			//创建TrackerClient对象
			TrackerClient trackerClient = new TrackerClient();
			//通过TrackerClient获取TrackerServer对象
			TrackerServer trackerServer = trackerClient.getConnection();
			//获取服务信息
			return trackerClient.getFetchStorages(trackerServer,groupName,remoteFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//获取Tracker服务地址
	public static String getTrackerUrl(){
		try {
			TrackerServer trackerServer = getTrackerServer();
			//获取Tracker地址
			return "http://"+trackerServer.getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static TrackerServer getTrackerServer() throws IOException {
		//创建TrackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		//通过TrackerClient获取TrackerServer对象
		return trackerClient.getConnection();
	}
}
