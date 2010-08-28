package org.realtors.rets.util.transaction.metadata;

import org.realtors.rets.common.metadata.Metadata;
import org.realtors.rets.common.metadata.types.MClass;
import org.realtors.rets.common.metadata.types.MTable;
import org.realtors.rets.util.ResourceClass;
import org.realtors.rets.util.RetsMetadataTransaction;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GetFields implements RetsMetadataTransaction<MTable[]> {
	private GetClass getClass;
    private Predicate fieldSelectionPredicate;
    
    public GetFields(ResourceClass resourceClass) {
        this(resourceClass, Predicates.<MTable>alwaysTrue());
    }

    public GetFields(ResourceClass resourceClass, Predicate<MTable> fieldSelectionPredicate) {
        assert fieldSelectionPredicate != null : "fieldSelectionPredicate cannot be null";
		
        this.getClass = new GetClass(resourceClass);
        this.fieldSelectionPredicate = fieldSelectionPredicate;
    }

    public MTable[] execute(Metadata metadata) throws Exception {
		MClass resourceClass = this.getClass.execute(metadata);
		return Iterables.toArray(Iterables.filter(Lists.newArrayList(resourceClass.getMTables()), this.fieldSelectionPredicate),MTable.class);
	}
}
