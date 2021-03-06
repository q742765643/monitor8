package com.piesat.common.jpa.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/19 15:38
 */
public class SimpleSpecification<T> implements Specification<T> {

    /**
     * 查询的条件列表，是一组列表
     */
    private List<SpecificationOperator> opers;

    public SimpleSpecification(List<SpecificationOperator> opers) {
        this.opers = opers;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        int index = 0;
        //通过resultPre来组合多个条件
        Predicate resultPre = null;
        for (SpecificationOperator op : opers) {
            if (index++ == 0) {
                resultPre = generatePredicate(root, criteriaBuilder, op);
                continue;
            }
            Predicate pre = generatePredicate(root, criteriaBuilder, op);
            if (pre == null) continue;
            if (SpecificationOperator.Join.and.name().equalsIgnoreCase(op.getJoin())) {
                resultPre = criteriaBuilder.and(resultPre, pre);
            } else if (SpecificationOperator.Join.or.name().equalsIgnoreCase(op.getJoin())) {
                resultPre = criteriaBuilder.or(resultPre, pre);
            }
        }
        return resultPre;
    }

    private Predicate generatePredicate(Root<T> root, CriteriaBuilder criteriaBuilder, SpecificationOperator op) {
        /*
         * 根据不同的操作符返回特定的查询*/
        if (SpecificationOperator.Operator.eq.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.equal(root.get(op.getKey()), op.getValue());
        } else if (SpecificationOperator.Operator.ge.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.ge(root.get(op.getKey()).as(Number.class), (Number) op.getValue());
        } else if (SpecificationOperator.Operator.le.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.le(root.get(op.getKey()).as(Number.class), (Number) op.getValue());
        } else if (SpecificationOperator.Operator.gt.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.gt(root.get(op.getKey()).as(Number.class), (Number) op.getValue());
        } else if (SpecificationOperator.Operator.lt.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.lt(root.get(op.getKey()).as(Number.class), (Number) op.getValue());
        } else if (SpecificationOperator.Operator.ges.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get(op.getKey()).as(String.class), String.valueOf(op.getValue()));
        } else if (SpecificationOperator.Operator.les.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.lessThanOrEqualTo(root.get(op.getKey()).as(String.class), String.valueOf(op.getValue()));
        } else if (SpecificationOperator.Operator.gts.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.greaterThan(root.get(op.getKey()).as(String.class), String.valueOf(op.getValue()));
        } else if (SpecificationOperator.Operator.lts.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.lessThan(root.get(op.getKey()).as(String.class), String.valueOf(op.getValue()));
        } else if (SpecificationOperator.Operator.likeAll.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(op.getKey()).as(String.class)), "%" + op.getValue().toString().toLowerCase() + "%");
        } else if (SpecificationOperator.Operator.likeL.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.like(root.get(op.getKey()).as(String.class), op.getValue() + "%");
        } else if (SpecificationOperator.Operator.likeR.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.like(root.get(op.getKey()).as(String.class), "%" + op.getValue());
        } else if (SpecificationOperator.Operator.isNull.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.isNull(root.get(op.getKey()));
        } else if (SpecificationOperator.Operator.isNotNull.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.isNotNull(root.get(op.getKey()));
        } else if (SpecificationOperator.Operator.notEqual.name().equalsIgnoreCase(op.getOper())) {
            return criteriaBuilder.notEqual(root.get(op.getKey()), op.getValue());
        } else if (SpecificationOperator.Operator.in.name().equalsIgnoreCase(op.getOper())) {
            return root.get(op.getKey()).in(op.getValue());
        } else if (SpecificationOperator.Operator.inn.name().equalsIgnoreCase(op.getOper())) {
            List<Integer> list = (List<Integer>) op.getValue();
            return root.get(op.getKey()).in(list);
        }
        return null;
    }
}


