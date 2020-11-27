package com.piesat.common.utils;

/**
 * @ClassName : CellModel
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-21 16:20
 */
public class CellModel {
    private String cellName;
    private Integer startRow;//起始行
    private Integer startColumn;//起始列
    private Integer endRow;//结束行
    private Integer endColumn;//结束列

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Integer getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(Integer startColumn) {
        this.startColumn = startColumn;
    }

    public Integer getEndRow() {
        return endRow;
    }

    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    public Integer getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(Integer endColumn) {
        this.endColumn = endColumn;
    }
}

