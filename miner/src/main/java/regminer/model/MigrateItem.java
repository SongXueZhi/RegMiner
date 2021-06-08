package regminer.model;

public class MigrateItem {
	private String commitID;
	private MigrateFailureType type;

	public MigrateItem() {

	}

	public MigrateItem(String commitID) {
		this.commitID = commitID;
	}

	public MigrateItem(String commitID, MigrateFailureType type) {
		this.commitID = commitID;
		this.type = type;
	}

	public enum MigrateFailureType {
		CompilationFailed {
			@Override
			public String getName() {
				return "CompilationFailed";
			}
		},
		NoTests {
			@Override
			public String getName() {
				return "NoTests";
			}
		},
		TESTSUCCESS {
			@Override
			public String getName() {
				return "TESTSUCCESS";
			}
		},
		NONE {
			@Override
			public String getName() {
				return "NONE";
			}
		};

		public abstract String getName();
	}
	public MigrateFailureType getType() {
		return type;
	}
	public void setType(MigrateFailureType type) {
		this.type = type;
	}
	public String getCommitID() {
		return commitID;
	}
	public void setCommitID(String commitID) {
		this.commitID = commitID;
	}

}
