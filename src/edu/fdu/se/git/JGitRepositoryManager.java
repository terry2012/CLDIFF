package edu.fdu.se.git;

import edu.fdu.se.config.ProjectProperties;
import edu.fdu.se.config.PropertyKeys;

public class JGitRepositoryManager {

	private static JGitTagCommand baseCmd;
	private static JGitTagCommand blueToothCmd;
	private static JGitTagCommand databindingCmd;
	private static JGitTagCommand mlCmd;

	private static String ANDROID_PLATFORM_FRAMEWORK_ROOT_PATH;
	static {
		ANDROID_PLATFORM_FRAMEWORK_ROOT_PATH = ProjectProperties.getInstance()
				.getValue(PropertyKeys.ANDROID_REPO_PATH2);
		baseCmd = new JGitTagCommand(
				ANDROID_PLATFORM_FRAMEWORK_ROOT_PATH + RepoConstants.platform_frameworks_base_ + ".git");
		blueToothCmd = new JGitTagCommand(
				ANDROID_PLATFORM_FRAMEWORK_ROOT_PATH + RepoConstants.platform_frameworks_opt_bluetooth_ + ".git");
		databindingCmd = new JGitTagCommand(
				ANDROID_PLATFORM_FRAMEWORK_ROOT_PATH + RepoConstants.platform_frameworks_data_binding_ + ".git");
		
		mlCmd = new JGitTagCommand(ANDROID_PLATFORM_FRAMEWORK_ROOT_PATH +RepoConstants.platform_frameworks_ml_ +".git");

	}

	public static JGitTagCommand getCommand(String key) {
		JGitTagCommand jtc;
		switch (key) {
		case RepoConstants.platform_frameworks_base_:
			jtc = baseCmd;
			break;
		case RepoConstants.platform_frameworks_opt_bluetooth_:
			jtc = blueToothCmd;
			break;
		case RepoConstants.platform_frameworks_data_binding_:
			jtc = databindingCmd;
			break;
		case RepoConstants.platform_frameworks_ml_:
			jtc = mlCmd;
			break;
		default:
			jtc = baseCmd;
			break;
		}
		return jtc;
	}
	
	public static JGitTagCommand getBaseCommand(){
		return baseCmd;
	}

}
