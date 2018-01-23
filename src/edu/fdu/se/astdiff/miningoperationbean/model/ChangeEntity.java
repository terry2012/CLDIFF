package edu.fdu.se.astdiff.miningoperationbean.model;

import com.github.javaparser.Range;
import edu.fdu.se.astdiff.miningoperationbean.ClusteredActionBean;

/**
 * Created by huangkaifeng on 2018/1/16.
 */
public class ChangeEntity {
    ClusteredActionBean clusteredActionBean;

    protected Range lineRange;

    protected int changeType;
    protected String changeEntity;
    /**
     * 因为存在复杂的内部类
     * String为A.B.c的形式
     *
     */
    protected String location;

    public ChangeEntity(){

    }

    public ChangeEntity(ClusteredActionBean bean){
        this.clusteredActionBean = bean;
        this.changeType = bean.operationType;
        this.changeEntity = bean.getOperationEntity();
    }




}