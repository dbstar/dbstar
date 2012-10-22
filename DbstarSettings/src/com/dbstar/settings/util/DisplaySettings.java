package com.dbstar.settings.util;

public class DisplaySettings {
	private static final String TAG = "DisplaySettings";
	
	private static final String STR_OUTPUT_VAR="ubootenv.var.outputmode";
	
	private final String[] mOutputModeList2 = 
		{
			"1080p60",
			"1080p25", 
			"1080i50",
			"720p",
			"auto"
		};
	
	    private final String[] mOutputModeList = 
		{
			"480i",  
			"480p",
			"576i",  
			"576p",         
			"720p",
			"1080i", 
			"1080p"
		};

		private final String[] mOutputModeList_50hz = 
		{
			"480i",  
			"480p",
			"576i",  
			"576p",         
			"720p50hz",
			"1080i50hz", 
			"1080p50hz"
		};
	
	public void setOutpuMode(String outputMode) {
		int[] curPosition = {0, 0, 1280, 720};
		
		boolean hasCvbsOutput = Utils.hasCVBSMode();
		
		if (hasCvbsOutput && (outputMode.equals("576i") || outputMode.equals("480i"))){
			disableVpp2();
		}
	}
	
	
	public void	disableVpp2()
	{
		Utils.setValue("/sys/class/display2/mode", "null");
//		SystemProperties.set("ubootenv.var.cvbsmode", "null");
	}
	
}
