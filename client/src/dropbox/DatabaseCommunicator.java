package dropbox;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.users.FullAccount;

/**
 * A class that provides methods for communicating with the Dropbox database.
 * 
 * @author Calvin Krist
 *
 */
public class DatabaseCommunicator {

	/**
	 * stores the log in into for the Dropbox account so we don't have to keep
	 * logging in
	 */
	private DbxClientV2 client;

	/**
	 * @param accessToken 		the token used to log in to the Dropbox account
	 * @throws DbxException		thrown if the login token is incorrect
	 */
	public DatabaseCommunicator(String accessToken) throws DbxException {
		// Get account and prove it is the correct account
		DbxRequestConfig config = new DbxRequestConfig("dropbox", "en_US");
		client = new DbxClientV2(config, accessToken);
	}
	
	/**
	 * Uploads a file to the Dropbox account
	 * 
	 * @param localFilePath		the file path that points to the file to be uploaded
	 * @param destFilePath		the file path the uploaded file with have once uploaded
	 */
	public void uploadByFilePath(String localFilePath, String destFilePath) {
		try (InputStream in = new FileInputStream(localFilePath)) {
			try {
				client.files().delete("/" + destFilePath);
			} finally {
				FileMetadata metadata = client.files().uploadBuilder("/" + destFilePath).uploadAndFinish(in);
			}
		} catch (DbxException | IOException e) {
			System.out.println("File not found.");
		} 
	}

	/**
	 * Downloads a file from the Dropbox account
	 * 
	 * @param localFilePath		the file path pointing to the downloaded file
	 * @param targetFilePath	the file path pointing to the file to be downloaded in the Dropbox account
	 */
	public void downloadFile(String localFilePath, String targetFilePath) {
		ListFolderResult result;
		try {
			result = client.files().listFolder("");
			boolean t = false;
			for(Metadata m : result.getEntries()) 
				if(m.getPathLower().equalsIgnoreCase("/" + targetFilePath))
					t = true;
			if(!t)
				return;
		} catch (DbxException e2) {
			e2.printStackTrace();
		}
		OutputStream out;
		try {
			out = new FileOutputStream(localFilePath);
			try {
				client.files().downloadBuilder("/" + targetFilePath).download(out);
			} catch (DbxException | IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Returns a file downloaded from the Dropbox database as a String.
	 * Can return null if the String doesn't exist.
	 * 
	 * @param localFilePath		the file path pointing to the downloaded file
	 * @param targetFilePath	the file path pointing to the file to be downloaded in the Dropbox account
	 */
	public String downloadFileAsString(String localFilePath, String targetFilePath) {
		ListFolderResult result;
		try {
			result = client.files().listFolder("");
			boolean t = false;
			for(Metadata m : result.getEntries()) 
				if(m.getPathLower().equalsIgnoreCase("/" + targetFilePath))
					t = true;
			if(!t)
				return null;
		} catch (DbxException e2) {
			e2.printStackTrace();
		}
		OutputStream out;
		try {
			out = new FileOutputStream(localFilePath);
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				client.files().download("/loginFile.txt").download(os);
				String aString = new String(os.toByteArray(),"UTF-8");
				return aString;
			} catch (DbxException | IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
