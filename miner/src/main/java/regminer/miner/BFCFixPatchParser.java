package regminer.miner;

import org.eclipse.jgit.diff.Edit;
import regminer.model.Methodx;
import regminer.model.NormalFile;
import regminer.model.PotentialRFC;
import regminer.utils.CompilationUtil;
import regminer.utils.FileUtilx;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BFCFixPatchParser {


    public void equipFixPatchToRFC(PotentialRFC pRFC) throws Exception {
        for (NormalFile file :pRFC.getNormalJavaFiles()){
            parse(file,pRFC.fileMap.get(pRFC.getCommit().getName()));
        }
        pRFC.setHaveEquipFixPatch(true);
    }
    public void parse(NormalFile normalFile, File bfcDir) throws FileNotFoundException {

        List<Methodx> methodxes = CompilationUtil.getAllMethod(FileUtilx.readContentFromFile(new File(bfcDir, normalFile.getNewPath())));
        List<Edit> edits = normalFile.getEditList();
        for (Edit edit : edits) {
            if (edit.getType()==Edit.Type.DELETE){
                continue;
            }
            matchAll(edit, methodxes, normalFile);
        }
    }

    private void matchAll(Edit edit, List<Methodx> methods, NormalFile normalFile) {
        for (Methodx method : methods) {
            match(edit, method, normalFile);
        }
    }

    private void match(Edit edit, Methodx method, NormalFile normalFile) {
        int editStart = edit.getBeginB() + 1;
        int editEnd = edit.getEndB();

        int methodStart = method.getStartLine();
        int methodStop = method.getStopLine();

        if ((editStart <= methodStart && editEnd >= methodStop) || (editStart >= methodStart && editEnd <= methodStop)
                || (editEnd >= methodStart && editEnd <= methodStop)
                || (editStart >= methodStart && editStart <= methodStop)) {
          normalFile.editMethodxes.add(method);
        }
    }


}
