package edu.fdu.se.ast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.github.gumtreediff.actions.ActionClusterFinder;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.TreeUtils;



public class GumTreeDiffParser {
	String oldFile;
	String newFile;
	public static TreeContext srcTC;
	public static TreeContext dstTC;
	ITree src;
	ITree dst;
	List<Action> actions;
	MappingStore mapping;
	ActionClusterFinder finder;
	
	public GumTreeDiffParser(String oldFile, String newFile){
		this.oldFile = oldFile;
		this.newFile = newFile;
	}
	
	public void init(){
		Run.initGenerators();
		try {
			JdtTreeGenerator parser = new JdtTreeGenerator();
			srcTC = parser.generateFromFile(new File(oldFile));
			src = srcTC.getRoot();
			dstTC = parser.generateFromFile(new File(newFile));
			dst = dstTC.getRoot();
			Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
			m.match();
			mapping = m.getMappings();
			ActionGenerator g = new ActionGenerator(src, dst, mapping);
			g.generate();
			actions = g.getActions();
			finder = new ActionClusterFinder(srcTC, dstTC, actions);
//			finder.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void listStartNodes(){
		for(Action a : finder.getStartNodes()){
			System.out.println(a.toString());
		}
	}
	public List<Set<Action>> getCluster(){
		return finder.getClusters();
	}
	public List<Action> getActions(){
		return actions;
	}
	public MappingStore getMapping(){
		return mapping;
	}
	public String getOldTreeString(){
		return src.toTreeString();
	}
	public String getPrettyOldTreeString(){
		return toTreeString(srcTC, src);
	}
	public String getNewTreeString(){
		return dst.toTreeString();
	}
	public String getPrettyNewTreeString(){
		return toTreeString(dstTC, dst);
	}
	public void printActions(List<Action> actions){
		System.out.println(actions.size());
		for(Action a : actions){
			printOneAction(a);
		}
//		for(int i = 0; i < 200; i++){
//			System.out.println(i+":"+srcTC.getTypeLabel(i));
//		}
	}
	public void printOneAction(Action a){
		if(a instanceof Delete){
			System.out.print("Delete>");
			Delete delete = (Delete)a;
			ITree deleteNode = delete.getNode();
			System.out.println(prettyString(dstTC,deleteNode)+" from "+prettyString(dstTC,deleteNode.getParent()));//old
			System.out.println(delete.toString());
		}
		if(a instanceof Insert){
			System.out.print("Insert>");
			Insert insert = (Insert)a;
			ITree insertNode = insert.getNode();
			System.out.println(prettyString(dstTC,insertNode)+" to "+prettyString(dstTC,insertNode.getParent())+" at "+ insert.getPosition());
//			System.out.println(prettyString(dstTC,insert.getParent()));//old
			System.out.println(insert.toString());
			System.out.println(insert.getParent()==insert.getNode().getParent());
		}
		if(a instanceof Move){
			System.out.print("Move>");
			Move move = (Move)a;
			ITree moveNode = move.getNode();
			System.out.println(prettyString(dstTC,moveNode)+" to "+prettyString(dstTC,move.getParent())+" at "+ move.getPosition());//old
			System.out.println(move.toString());

			System.out.println("----------------Move----------------------------");
			System.out.println(prettyString(dstTC, move.getParent()));
		}
		if(a instanceof Update){
			System.out.print("Update>");
			Update update = (Update)a;
			ITree updateNode = update.getNode();
			System.out.println("from "+updateNode.getLabel()+" to "+update.getValue());//old
		}
		System.out.println("----------------Node----------------------------");
		System.out.println(prettyString(dstTC, a.getNode()));//move-old,update-old,insert-new,delete-old
//		System.out.println(toTreeString(dstTC, a.getNode()));
		System.out.println("-----------------Parent---------------------------");
		System.out.println(prettyString(dstTC, a.getNode().getParent()));//move-old,update-old,insert-new,delete-old
//		System.out.println(toTreeString(dstTC, a.getNode().getParent()));
		System.out.println("============================================");
	}
	
	public static String prettyString(TreeContext con, ITree node){
		return node.getId()+". "+con.getTypeLabel(node)+":"+node.getLabel();
	}
	private String indent(ITree t) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < t.getDepth(); i++)
            b.append("\t");
        return b.toString();
    }
    public String toTreeString(TreeContext con, ITree tree) {
        StringBuilder b = new StringBuilder();
        for (ITree t : TreeUtils.preOrder(tree))
            b.append(indent(t)+t.toShortString()+" | " + prettyString(con, t) + "\n");
        return b.toString();
    }
    
	public static void main(String[] args) {
//		String file1 = "src/test/java/resources/StringBuilderCase1.java";
//		String file2 = "src/test/java/resources/StringBuilderCase2.java";
//		String file1 = "src/test/java/resources/StringEquals1.java";
//		String file2 = "src/test/java/resources/StringEquals2.java";
//		String file1 = "src/test/java/resources/CloseCase1.java";
//		String file2 = "src/test/java/resources/CloseCase2.java";
//		String file1 = "src/test/java/resources/DeleteFileCase1.java";
//		String file2 = "src/test/java/resources/DeleteFileCase2.java";
//		String file1 = "src/test/java/resources/ClassChangeCase1.java";
//		String file2 = "src/test/java/resources/ClassChangeCase2.java";
		String file2 = "C:/Users/huangkaifeng/Desktop/11-8/test/curr/GumTreePaperExample.java";
		String file1 = "C:/Users/huangkaifeng/Desktop/11-8/test/prev/GumTreePaperExample.java";
		GumTreeDiffParser diff = new GumTreeDiffParser(file1,file2);
		diff.init();
//		System.out.println("---------------------------------Old Tree------------------------------------");
//		System.out.println(diff.getOldTreeString());
//		System.out.println("---------------------------------New Tree------------------------------------");
//		System.out.println(diff.getNewTreeString());
//		System.out.println("---------------------------------------------------------------------");
		System.out.println("---------------------------------Old Tree------------------------------------");
		System.out.println(diff.getPrettyOldTreeString());
		System.out.println("---------------------------------New Tree------------------------------------");
		System.out.println(diff.getPrettyNewTreeString());
		System.out.println("###############################Actions----------------------------------");
		diff.printActions(diff.getActions());
		diff.listStartNodes();
//		int count = 0;
//		for(Set<Action> as : diff.getCluster()){
//			System.out.println("***************************************************"+count+"********************************************");
//			for(Action a: as){
//				diff.printOneAction(a);
//			}
//			count++;
//		}
	}

}