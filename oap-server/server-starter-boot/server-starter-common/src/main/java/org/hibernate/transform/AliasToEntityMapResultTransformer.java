//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.hibernate.transform;

import com.piesat.common.config.DatabseType;

import java.util.HashMap;
import java.util.Map;

public class AliasToEntityMapResultTransformer extends AliasedTupleSubsetResultTransformer {
    public static final AliasToEntityMapResultTransformer INSTANCE = new AliasToEntityMapResultTransformer();

    private AliasToEntityMapResultTransformer() {
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map result = new HashMap(tuple.length);

        for (int i = 0; i < tuple.length; ++i) {
            String alias = aliases[i];
            if (alias != null) {
                if ("postgresql".equals(DatabseType.type)) {
                    result.put(alias.toUpperCase(), tuple[i]);
                } else {
                    result.put(alias, tuple[i]);
                }

            }
        }

        return result;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}
