package edu.fdu.se.git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

import edu.fdu.se.astdiff.preprocessingfile.data.FileOutputLog;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import edu.fdu.se.config.ProjectProperties;
import edu.fdu.se.config.PropertyKeys;

public class JGitRepositoryCommand extends JGitCommand {

    public JGitRepositoryCommand(String repopath) {
        super(repopath);
    }




    public void walkRepoFromBackwards(String outDir) {
        try {
//            FileOutputLog fileOutputLog = new FileOutputLog(outDir);
            Queue<RevCommit> commitQueue = new LinkedList<>();
            Map<String, Boolean> isTraversed = new HashMap<>();
            List<Ref> mList = this.git.branchList().setListMode(ListMode.ALL).call();
            for (Ref item : mList) {
                RevCommit commit = revWalk.parseCommit(item.getObjectId());
                commitQueue.offer(commit);
                while (commitQueue.size() != 0) {
                    RevCommit queueCommitItem = commitQueue.poll();
                    RevCommit[] parentCommits = queueCommitItem.getParents();
                    if (isTraversed.containsKey(queueCommitItem.getName()) || parentCommits == null) {
                        continue;
                    }
                    Map<String, Map<String, List<String>>> changedFiles = this.getCommitParentMappedFileList(queueCommitItem.getName());
                    for (Entry<String, Map<String, List<String>>> entry : changedFiles.entrySet()) {
                        String parentCommitId = entry.getKey();
                        Map<String, List<String>> changedFileEntry = entry.getValue();
                        if (changedFileEntry.containsKey("modifiedFiles")) {
                            List<String> modifiedFile = changedFileEntry.get("modifiedFiles");
                            for (String file : modifiedFile) {
                                if (!file.endsWith(".java")) {
                                    continue;
                                }
                                byte[] prevFile = this.extract(file, parentCommitId);
                                byte[] currFile = this.extract(file, queueCommitItem.getName());
                                int index = file.lastIndexOf("/");
                                String fileName = file.substring(index + 1, file.length() - 1);
//                                fileOutputLog.writeRQ1CommitFile(prevFile, currFile, parentCommitId + "-" + queueCommitItem.getName(), fileName);
                            }
                        }
                    }
                    isTraversed.put(queueCommitItem.getName(), true);
                    for (RevCommit item2 : parentCommits) {
                        RevCommit commit2 = revWalk.parseCommit(item2.getId());
                        commitQueue.offer(commit2);
                    }
                    break;
                }
            }
            System.out.println("CommitSum:" + isTraversed.size());
        } catch (MissingObjectException e) {
            e.printStackTrace();
        } catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e1) {
            e1.printStackTrace();
        }

    }

    public static void main(String args[]) {
        JGitRepositoryCommand cmd = new JGitRepositoryCommand(
                ProjectProperties.getInstance().getValue(PropertyKeys.ANDROID_REPO_PATH2) + RepoConstants.platform_frameworks_base_ + ".git");
//		cmd.getCommitParentMappedFileList2("cd97c0e935d13bbd29dce0417093ec694c3ddd76");
//		CommitCodeInfo cci = cmd.getCommitFileEditSummary("c7f502947b5b80baca084101fb7a0aaa74db9974", JGitCommand.JAVA_FILE);
    }


    public Map<String, Map<String, List<String>>> test(String commmitid) {
        Map<String, Map<String, List<String>>> result = new HashMap<String, Map<String, List<String>>>();
        ObjectId commitId = ObjectId.fromString(commmitid);
        RevCommit commit = null;

        try {
            commit = revWalk.parseCommit(commitId);
            RevCommit[] parentsCommits = commit.getParents();
            for (RevCommit parent : parentsCommits) {
                ObjectReader reader = git.getRepository().newObjectReader();
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                ObjectId newTree = commit.getTree().getId();
                newTreeIter.reset(reader, newTree);
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                RevCommit pCommit = revWalk.parseCommit(parent.getId());
                ObjectId oldTree = pCommit.getTree().getId();
                oldTreeIter.reset(reader, oldTree);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
//				DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
                DiffFormatter diffFormatter = new DiffFormatter(out);
                diffFormatter.setRepository(git.getRepository());
                List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
                diffFormatter.setContext(0);
                for (DiffEntry entry : entries) {
                    switch (entry.getChangeType()) {
                        case ADD:
                            break;
                        case MODIFY:
                        case DELETE:
//						this.getCommitEditScript(null, entry);	
//			             RawText r = new RawText(out.toByteArray());
//			               r.getLineDelimiter();
                            System.out.println(out.toString());
                            out.reset();
                            break;

                        default:
                            break;
                    }
                }
                diffFormatter.close();
            }
            return result;
        } catch (MissingObjectException e) {
            e.printStackTrace();
        } catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
