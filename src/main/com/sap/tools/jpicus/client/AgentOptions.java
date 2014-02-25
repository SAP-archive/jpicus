package com.sap.tools.jpicus.client;

public interface AgentOptions {

	public abstract boolean getKeepClosedHandles();

	public abstract boolean getTrackIO();

	public abstract boolean getTrackSuccessfulDelete();

	public abstract boolean getTrackFailedDelete();

	public abstract boolean getDisableThreadDetails();

	public abstract boolean isCalibrationEnabled();

	public abstract boolean dumpStateOnExit();

	public abstract boolean getCollectDeleteOfNonExisting();

	public abstract int getStackTraceLimit();

	public abstract boolean isVerbose();

}