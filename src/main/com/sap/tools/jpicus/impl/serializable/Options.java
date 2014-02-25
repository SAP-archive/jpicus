package com.sap.tools.jpicus.impl.serializable;

import java.io.Serializable;
import java.util.StringTokenizer;

import com.sap.tools.jpicus.client.AgentOptions;

public class Options implements Serializable, AgentOptions {
	
	private static final long serialVersionUID = 1L;

	private static Options INSTANCE = new Options();

	private boolean keepClosedHandles;
	private boolean trackSuccessfulDelete;
	private boolean trackFailedDelete;
	private boolean trackIO;
	private boolean calibrate;
	private boolean dumpStateOnExit;
	private boolean collectDeleteOfNonExisting;
	
	private boolean disableThreadDetails;
	private int stackTraceLimit;
	private int port = 21500;
	
	private boolean verbose;
	
	private Options(){
		
	}

	
	public static synchronized void parse(String args) {

		INSTANCE = new Options(); // cleanup previous state
		if(args == null){
			return;
		}
	
		StringTokenizer tokenizer = new StringTokenizer(args, ";");
		
		while( tokenizer.hasMoreTokens() ){
			
			String token = tokenizer.nextToken();
			if (token.equals("keepClosedHandles")){
				INSTANCE.keepClosedHandles = true;
			}
			if (token.equals("trackFailedDelete")){
				INSTANCE.trackFailedDelete = true;
			}
			if (token.equals("trackSuccessfulDelete")){
				INSTANCE.trackSuccessfulDelete = true;
			}
			if (token.equals("trackIO")){
				INSTANCE.enableTrackIO();
			}
			
			if (token.equals("disableThreadDetails")){
				INSTANCE.disableThreadDetails = false;
			}
			
			if (token.equals("collectDeleteOfNonExisting")){
				INSTANCE.collectDeleteOfNonExisting = true;
			}
			
			if (token.equals("dumpStateOnExit")){
				INSTANCE.dumpStateOnExit = true;
			}
			
			if (token.equals("verbose")){
				INSTANCE.verbose = true;
			}
			
			if (token.contains("stackTraceLimit")){
				StringTokenizer tok = new StringTokenizer(token, "=");
				if(tok.hasMoreTokens() && tok.nextToken().equals("stackTraceLimit")){
					if(tok.hasMoreTokens()){
						String limitString = tok.nextToken();
						try{
							INSTANCE.stackTraceLimit = Integer.parseInt(limitString);	
						} catch (NumberFormatException nfe){
							nfe.printStackTrace();
						}
						
					}
				}
			}
			
			if (token.contains("port")){
				StringTokenizer tok = new StringTokenizer(token, "=");
				if(tok.hasMoreTokens() && tok.nextToken().equals("port")){
					if(tok.hasMoreTokens()){
						String portString = tok.nextToken();
						try{
							INSTANCE.port = Integer.parseInt(portString);	
						} catch (NumberFormatException nfe){
							nfe.printStackTrace();
						}
						
					}
				}
			}
			
		}
			
	}
	
	public static Options getInstance(){
		return INSTANCE;
	}

	
	public int getPort(){
		return port;
	}

	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#getKeepClosedHandles()
	 */
	public boolean getKeepClosedHandles() {
		return keepClosedHandles;
	}

	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#getTrackIO()
	 */
	public boolean getTrackIO() {
		return trackIO;
	}

	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#getTrackSuccessfulDelete()
	 */
	public boolean getTrackSuccessfulDelete() {
		return trackSuccessfulDelete;
	}

	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#getTrackFailedDelete()
	 */
	public boolean getTrackFailedDelete() {
		return trackFailedDelete;
	}

	private void enableTrackIO() {
		trackIO = true;
		keepClosedHandles = true;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#getDisableThreadDetails()
	 */
	public boolean getDisableThreadDetails() {

		return disableThreadDetails;
	}

	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#isCalibrationEnabled()
	 */
	public boolean isCalibrationEnabled() {
		
		return calibrate;
	}

	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#dumpStateOnExit()
	 */
	public boolean dumpStateOnExit() {

		return dumpStateOnExit;
	}

	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#getCollectDeleteOfNonExisting()
	 */
	public boolean getCollectDeleteOfNonExisting() {

		return collectDeleteOfNonExisting;
	}
	
	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#getStackTraceLimit()
	 */
	public int getStackTraceLimit(){
		return stackTraceLimit;
	}


	/* (non-Javadoc)
	 * @see com.sap.tools.jpicus.impl.serializable.AgentOptions#isVerbose()
	 */
	public boolean isVerbose() {
		return verbose;
	}
	
	
	
}
