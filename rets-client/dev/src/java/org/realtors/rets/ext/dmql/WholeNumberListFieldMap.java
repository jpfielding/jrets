package org.realtors.rets.ext.dmql;

import java.math.BigInteger;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/** Sense handled by the value */
public class WholeNumberListFieldMap extends FieldMap<WholeNumberList> {
	public WholeNumberListFieldMap(String name, WholeNumberList value) {
		super(name, value);
	}

	@Override
	public String toString() {
		WholeNumberList integerList = this.getValue();
		if(LookupType.AND.equals(integerList.getEquality()))
			throw new IllegalArgumentException(LookupType.AND + " makes no sense for a list of numbers");

		Iterable<BigInteger> prunedValues = Iterables.filter(integerList.getValues(), Predicates.notNull());
		if(LookupType.NOT.equals(integerList.getEquality()))
			return this.createNotAndFragment(prunedValues);
		return this.createEqualsOrFragment(prunedValues);
	}

	//-->Helpers
	private interface OperandCreator {
		String createOperand(BigInteger value);
	}

	private String createNotAndFragment(Iterable<BigInteger> prunedValues) {
		Iterable<String> pieces = createPieces(prunedValues, new OperandCreator() {
			public String createOperand(BigInteger value) {
				String upperRange = (value.add(new BigInteger("1"))) + "+";
				if(value.compareTo(new BigInteger("0")) <= 0)
					return upperRange;
				return value.subtract(new BigInteger("1")) + "-," + upperRange;
			}
		});
		return Joiner.on(",").join(pieces);
	}

	private String createEqualsOrFragment(Iterable<BigInteger> prunedValues) {
		Iterable<String> pieces = createPieces(prunedValues, new OperandCreator() {
			public String createOperand(BigInteger value) {
				return value.toString();
			}
		});

		if(Iterables.size(pieces) == 1)
			return pieces.iterator().next();

		return "(" + Joiner.on(",").join(pieces) + ")";
	}

	private Iterable<String> createPieces(Iterable<BigInteger> prunedValues, final OperandCreator pieceMaker) {
		return Iterables.transform(prunedValues, new Function<BigInteger,String>(){
			public String apply(BigInteger input) {
				return "(" + getName() + "=" + pieceMaker.createOperand(input) + ")";
			}});
	}
}
