import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

public class BFCDetect {

	public static void main(String[] args) throws Exception {
		String clientPath=args[0];
		Repository repo = new FileRepository(clientPath);
		PotentialBFCDetector detecor = new PotentialBFCDetector(repo, new Git(repo));
		int num = detecor.detectPotentialBFC().size();
		System.out.println("bfc num :"+num);
	}

	//从文件列表中批量处理
	public void batchHandle(){
		TestExecutor exec = new TestExecutor();
		File csv = new File("passsite.csv"); // CSV文件路径
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csv));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = "";
		int i = 0;
		try {
			while ((line = br.readLine()) != null) // 读取到的内容给line变量
			{
				i++;
				if (line.contains("_id") || i == 1) {
					FileUtils.write(new File("new_result.csv"), line + "," + "bfc_num", true);
					System.out.println(line + "," + "bfc_num");
					continue;
				}
				String url = line.split(",")[1];
				Repository repo = new RepositoryProvider(url).get("tmp_" + i);
				PotentialBFCDetector detecor = new PotentialBFCDetector(repo, new Git(repo));
				int num = detecor.detectPotentialBFC().size();
				FileUtils.write(new File("new_result.csv"), line + "," + num, true);
				System.out.println(line + "," + num);
			}
			System.out.println("End!");
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
		}
	}
}
