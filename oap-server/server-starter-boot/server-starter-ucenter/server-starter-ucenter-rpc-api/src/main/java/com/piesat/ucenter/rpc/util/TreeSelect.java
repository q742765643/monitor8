package com.piesat.ucenter.rpc.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.piesat.ucenter.rpc.dto.system.DeptDto;
import com.piesat.ucenter.rpc.dto.system.MenuDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author ruoyi
 */
@Data
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID
     */
    private String id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect() {

    }

    public TreeSelect(DeptDto dept) {
        this.id = dept.getId();
        this.label = dept.getDeptName();
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(MenuDto menuDto) {
        this.id = menuDto.getId();
        this.label = menuDto.getMenuName();
        this.children = menuDto.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

}
