package regminer.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class NormalFile extends ChangedFile {

	public NormalFile(String newPath) {
		super(newPath);
	}

	public Set<Methodx> editMethodxes =new LinkedHashSet<>();

}
