package com.sap.tools.jpicus.client;

import java.io.Serializable;

/**
 * TODO detect partitions and generate individual 
 * calibration data or resort to native tools
 * @author pavel
 *
 */
public interface CalibrationData extends Serializable{

	String getDescription();
	
}
