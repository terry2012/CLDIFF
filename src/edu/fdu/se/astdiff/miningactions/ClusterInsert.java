package edu.fdu.se.astdiff.miningactions;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.tree.ITree;
import edu.fdu.se.astdiff.generatingactions.ActionConstants;
import edu.fdu.se.astdiff.miningoperationbean.HighLevelOperationBean;

/**
 * Created by huangkaifeng on 2018/1/13.
 */
public class ClusterInsert {

    public static void findInsert(MiningActionData fp) {
        int insertActionCount = fp.mGeneratingActionsData.getInsertActions().size();
        int insertActionIndex = 0;
        while (true) {
            if (insertActionIndex == insertActionCount) {
                break;
            }
            Action a = fp.mGeneratingActionsData.getInsertActions().get(insertActionIndex);
            insertActionIndex++;
            if (fp.mGeneratingActionsData.getAllActionMap().get(a) == 1) {
                // 标记过的action
                continue;
            }
            Insert ins = (Insert) a;
            ITree insNode = ins.getNode();
            String type = fp.mDstTree.getTypeLabel(insNode);
            ITree fafafather = AstRelations.findFafafatherNode(insNode, fp.mDstTree);
            if(fafafather == null){
                System.out.println("Father Null Condition: "+ ActionConstants.getInstanceStringName(a) + " " +type);
                fp.setActionTraversedMap(a);
                continue;
            }
            String fatherType = fp.mDstTree.getTypeLabel(fafafather);
//			String nextAction = ConsolePrint.getMyOneActionString(a, 0, this.mDstTree);
//			System.out.print(nextAction);
            HighLevelOperationBean operationBean;
            if(StatementConstants.FIELDDECLARATION.equals(type)){
                //insert FieldDeclaration
                operationBean = MatchFieldDeclaration.matchFieldDeclaration(fp,a,type);
                fp.mHighLevelOperationBeanList.add(operationBean);
                continue;
            } else if(StatementConstants.FIELDDECLARATION.equals(fatherType)){
                //insert FieldDeclaration body
                operationBean = MatchFieldDeclaration.matchFieldDeclarationByFather(fp,a,type,fafafather,fatherType);
                fp.mHighLevelOperationBeanList.add(operationBean);
                continue;
            }

            if (StatementConstants.METHODDECLARATION.equals(type)) {
                operationBean = MatchNewOrDeleteMethod.matchNewOrDeleteMethod(fp,a,type);
                fp.mHighLevelOperationBeanList.add(operationBean);
                continue;
            } else if (StatementConstants.METHODDECLARATION.equals(fatherType)) {
                System.out.println(insNode.getParent().getChildPosition(insNode));
                operationBean = MatchMethodSignatureChange.matchMethodSignatureChange(fp,a, type,fafafather,fatherType);
                fp.mHighLevelOperationBeanList.add(operationBean);
            } else {
                // 方法体
                switch (type) {
                    case StatementConstants.IFSTATEMENT:
                        // Pattern 1. Match If/else if
                        operationBean = MatchIfElse.matchIf(fp,a, type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.BLOCK:
                        MatchBlock.matchBlock(fp,a, type,fp.mDstTree);
                        break;
                    case StatementConstants.BREAKSTATEMENT:
                        if(AstRelations.isFatherSwitchStatement(a, fp.mDstTree)) {
                            //增加switch语句
                            operationBean = MatchSwitch.matchSwitchCase(fp, a, type, fafafather, fatherType);
                            fp.mHighLevelOperationBeanList.add(operationBean);
                        }else {
                            System.out.println("Other Condition"+ActionConstants.getInstanceStringName(a) + " " +type);
                            fp.setActionTraversedMap(a);
                            // TODO剩下的情况
                        }
                        break;
                    case StatementConstants.RETURNSTATEMENT:
                        MatchReturnStatement.matchReturnStatement(fp,a, type,fp.mDstTree);
                        break;
                    case StatementConstants.FORSTATEMENT:
                        //增加for语句
                        operationBean = MatchForStatement.matchForStatement(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.ENHANCEDFORSTATEMENT:
                        //增加for语句
                        operationBean = MatchForStatement.matchEnhancedForStatement(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.WHILESTATEMENT:
                        //增加while语句
                        operationBean = MatchWhileStatement.matchWhileStatement(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.DOSTATEMENT:
                        //增加do while语句
                        operationBean = MatchWhileStatement.matchDoStatement(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.TRYSTATEMENT:
                        operationBean = MatchTry.matchTry(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.THROWSTATEMENT:
                        operationBean = MatchTry.matchThrowStatement(fp,a,type,fafafather,fatherType);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.VARIABLEDECLARATIONSTATEMENT:
                        operationBean = MatchVariableDeclarationExpression.matchVariableDeclaration(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.EXPRESSIONSTATEMENT:
                        if(AstRelations.isFatherIfStatement(a, fp.mDstTree)) {
                            // Pattern 1.2 Match else
                            operationBean = MatchIfElse.matchElse(fp, a,type,fafafather,fatherType);
                            fp.mHighLevelOperationBeanList.add(operationBean);
                        }
                        else {
                            operationBean = MatchExpressionStatement.matchExpression(fp, a,type,fafafather,fatherType);
                            fp.mHighLevelOperationBeanList.add(operationBean);
                        }
                        break;
                    case StatementConstants.CONDITIONALEXPRESSION:
                        operationBean = MatchConditionalExpression.matchConditionalExpression(fp, a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.SYNCHRONIZEDSTATEMENT:
                        //同步语句块增加
                        operationBean = MatchSynchronized.matchSynchronized(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.SWITCHSTATEMENT:
                        //增加switch语句
                        operationBean = MatchSwitch.matchSwitch(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.SWITCHCASE:
                        //增加switch语句
                        operationBean = MatchSwitch.matchSwitchCase(fp,a,type,fafafather,fatherType);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    case StatementConstants.JAVADOC:
                        //增加javadoc
                        operationBean = MatchJavaDoc.matchJavaDoc(fp,a,type);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        break;
                    //JAVADOC参数
                    case StatementConstants.TAGELEMENT:
                    case StatementConstants.TEXTELEMENT:
                        // 方法参数
                    case StatementConstants.SIMPLENAME:
                    case StatementConstants.STRINGLITERAL:
                    case StatementConstants.NULLLITERAL:
                    case StatementConstants.CHARACTERLITERAL:
                    case StatementConstants.NUMBERLITERAL:
                    case StatementConstants.BOOLEANLITERAL:
                    case StatementConstants.INFIXEXPRESSION:
                    case StatementConstants.METHODINVOCATION:
                        MatchSimpleNameOrLiteral.matchSimplenameOrLiteral(fp,a, type,fp.mDstTree);
                        break;
                    default:
                        String operationEntity = "DEFAULT: "+ ActionConstants.getInstanceStringName(a) + " " +type;
                        operationBean = new HighLevelOperationBean(a,type,null,-1,operationEntity,fafafather,fatherType);
                        fp.mHighLevelOperationBeanList.add(operationBean);
                        fp.setActionTraversedMap(a);
                        break;
                }
            }
        }
    }
}
