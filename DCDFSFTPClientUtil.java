package com.fd.bing.ad.push.ftp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class DCDFSFTPClientUtil {
	
	private static String STRICTHOSTKEYCHECKING = "StrictHostKeyChecking";
	
	private static String STRICTHOSTKEYCHECKING_VALUE = "no";
	
	private static int PORT = 22;
	
	private static String CHANNEL = "sftp";

	private static final Logger logger = LoggerFactory.getLogger(DCDFSFTPClientUtil.class);

	public List<String> getFileList(String SFTPWORKINGDIR, String server, String user, String password) {
		List<String> list = new ArrayList<String>();
		JSch jsch = new JSch();
		Session session = null;
		try {
			logger.info("server " + server);
			logger.info("user " + user);
			// logger.info("password " + password);

			session = jsch.getSession(user, server, PORT);
			session.setConfig(STRICTHOSTKEYCHECKING, STRICTHOSTKEYCHECKING_VALUE);
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel(CHANNEL);
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(SFTPWORKINGDIR);
			Vector<?> filelist = sftpChannel.ls(SFTPWORKINGDIR);
			for (int i = 0; i < filelist.size(); i++) {
				LsEntry entry = (LsEntry) filelist.get(i);

				if (entry.getFilename().toLowerCase().endsWith(".csv")) {
					logger.info(entry.getFilename());
					list.add(entry.getFilename());
				}
			}
			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			// e.printStackTrace();
			logger.error("Error in Connecting to SFTP Server ::", e);
		} catch (SftpException e) {
			// e.printStackTrace();
			logger.error("Error in SFTP Server to get File List ::", e);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in get File List from SFTP server ::", e);
		}
		return list;
	}

	public boolean getFile(String server, String user, String password, String inputFile, String outputFile) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			logger.info("server " + server);
			logger.info("user " + user);
			// logger.info("password " + password);
			logger.info("input file " + inputFile);
			logger.info("output path " + outputFile);
			session = jsch.getSession(user, server, PORT);
			session.setConfig(STRICTHOSTKEYCHECKING, STRICTHOSTKEYCHECKING_VALUE);
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel(CHANNEL);
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.get(inputFile, outputFile);
			sftpChannel.exit();
			session.disconnect();
			logger.info("Server : " + server + " Path:" + outputFile + " User:" + user);
		} catch (JSchException e) {
			logger.error("Error in Connecting to server : " + server + " Path:" + outputFile + " User:" + user +"::",e);
			// e.printStackTrace();
			return false;
		} catch (SftpException e) {
			// e.printStackTrace();
			logger.error("Error in getting the file from Server : " + server + " Path:" + outputFile + " User:" + user +"::",e);
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in getting the file from Server : " + server + " Path:" + outputFile + " User:" + user +"::",e);
			return false;
		}
		return true;
	}

	public boolean putFile(String server, String user, String password, String inputFile, String outputFile) {
		JSch jsch = new JSch();
		Session session = null;

		try {
			logger.info("server " + server);
			logger.info("user " + user);
			logger.info("input file " + inputFile);
			logger.info("output path " + outputFile);
			session = jsch.getSession(user, server, PORT);
			session.setConfig(STRICTHOSTKEYCHECKING, STRICTHOSTKEYCHECKING_VALUE);
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel(CHANNEL);
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			// sftpChannel.get("remotefile.txt", "localfile.txt");
			InputStream input = new FileInputStream(inputFile);
			sftpChannel.put(input, outputFile);
			sftpChannel.exit();
			session.disconnect();
			logger.info("Server : " + server + " Path:" + outputFile + " User:" + user);
		} catch (JSchException e) {
			// e.printStackTrace();
			logger.error(
					"Error in Connecting to server to push file : " + server + " Path:" + outputFile + " User:" + user +"::",e);
			return false;
		} catch (SftpException e) {
			// e.printStackTrace();
			logger.error("Error in sftp to push file to server : " + server + " Path:" + outputFile + " User:" + user +"::",e);
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("Error in push file to server : " + server + " Path:" + outputFile + " User:" + user +"::",e);
			return false;
		}
		return true;
	}

	public boolean deleteFile(String fileName, String server, String user, String password) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(user, server, PORT);
			session.setConfig(STRICTHOSTKEYCHECKING, STRICTHOSTKEYCHECKING_VALUE);
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel(CHANNEL);
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.rm(fileName);
			sftpChannel.exit();
			session.disconnect();
			logger.info("File Removed : " + fileName + ", Server : " + server);
		} catch (JSchException | SftpException e) {
			//e.printStackTrace();
			logger.error("Failed to Connect to Server to Remove File : " + fileName +"::",e);
			return false;
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Failed to Remove : " + fileName +"::",e);
			return false;
		}
		return true;

	}
}
