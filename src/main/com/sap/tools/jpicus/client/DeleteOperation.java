package com.sap.tools.jpicus.client;

import java.io.Serializable;
import java.util.Set;

/**
 * Describes a file delete operation.
 * @author pavel
 *
 */
public interface DeleteOperation extends Serializable {
	
	/**
	 * 
	 * @return true if the operation was successful. 
	 */
	boolean isSuccessful();
	
	/**
	 * @return information about the thread attempting the operation
	 */
	ThreadInfo getAttemptingThreadInfo();
	
	
	/**
	 * 
	 * @return a set of handles that caused the failure of the operation.
	 * For example, if there was a FileInputStream currently opened that
	 * prevented the deletion of the file, its descriptor shall be in 
	 * this collection. The result may or may not be an empty set for successful
	 * operations. This is so because some operating systems (*nix) have only 
	 * advisory locking and nothing prevents an incorrect program from deleting
	 * a currently opened file.
	 */
	Set<HandleDescriptor> getAssociatedHandleDescriptors();
	
	/**
	 * 
	 * @return a textual description with additional information for the operation
	 * or null if none
	 */
	String getDescription();
	
}
