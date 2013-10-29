package jhelp.math.quantile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

public class Estimator<T> {
	static public final Comparator<Sample<?>> COMPARATOR_WEIGHTED = new Comparator<Sample<?>>() {
		@Override
		public int compare(Sample<?> o1, Sample<?> o2) {
			return o2.weight - o1.weight;
		}
	};
	
	private final int desiredCut;
	private final int maxSamples;
	private final List<Sample<T>> byOrder;
	private final List<Sample<T>> byWeight;
	private final Comparator<Sample<T>> comparatorNatualy;
	private final Dividable<T> dividable;
	
	static public interface Dividable<T> extends Comparator<T> {
		T divide(T a, T b); //returns (a+b)/2
	}

	public Estimator(Dividable<T> dividable, int cut) {
		this(dividable, cut, 0.9f);
	}
	
	public Estimator(final Dividable<T> dividable, int cut, float precision) {
		Preconditions.checkArgument(cut >= 1 && precision > 0 && precision < 1);
		this.desiredCut = cut;
		this.maxSamples = (int) (1.0f / (1 - precision) * cut);
		this.byOrder = new ArrayList<Sample<T>>(maxSamples+2);
		this.byWeight = new ArrayList<Sample<T>>(maxSamples+2);
		this.comparatorNatualy = new Comparator<Sample<T>>() {
			@Override
			public int compare(Sample<T> o1, Sample<T> o2) {
				return dividable.compare(o1.value, o2.value);
			}
		};
		this.dividable = dividable;
	}
	
	public void sampling(T value, int weight) {
		Preconditions.checkArgument(weight > 0);
		Sample<T> sample = new Sample<T>(value, 0);
		final int k = Collections.binarySearch(byOrder, sample, comparatorNatualy);
		if (k >= 0 && k < byOrder.size()) {
			adjustWeight(byOrder.get(k), weight);
		} else {
			if (byOrder.size() < maxSamples) {
				if (k < 0) {
					byOrder.add(-1 - k, sample);
				} else if (k == byOrder.size()) {
					byOrder.add(sample);
				}
				byWeight.add(sample);
				adjustWeight(sample, weight);
			} else {
				int n = -1-k;  //insertion point
				if (n == byOrder.size()) {
					n--;
					byOrder.get(n).value = value;
				}
				sample = byOrder.get(n);
				adjustWeight(sample, weight);
			}
		}
		balance();
	}
	
	public List<T> getEstimation() throws SampleUnderrunException {
		Preconditions.checkState(Ordering.from(comparatorNatualy).isOrdered(byOrder));
		Preconditions.checkState(Ordering.from(COMPARATOR_WEIGHTED).isOrdered(byWeight));
		if (byOrder.size() < desiredCut) {
			throw new SampleUnderrunException("At most "+byOrder.size()+" cut is possible instead of "+desiredCut);
		}
		final int M = byOrder.size();
		final int half1 = byOrder.get(0).weight / 2;
		final int half2 = M > 1 ? byOrder.get(M-1).weight / 2 : 0;
		final long grossWeight = sumWeight(0, M) + half1 + half2;
		final double desiredWeight = 1.0 * grossWeight / desiredCut;
		int[] splits = new int[desiredCut-1];
		
		for (Sample<T> sam : byOrder) {
			System.out.println("by order: " + sam.weight);
		}
		for (Sample<T> sam : byWeight) {
			System.out.println("by weight: " + sam.weight);
		}
		for (int i=0, index=0; i<splits.length; i++) {
			long weight = byOrder.get(index++).weight + (i == 0 ? half1 : 0);
			while (index < M-1) {
				Sample<T> next = byOrder.get(index);
				if (weight + next.weight > desiredWeight) {
					break;
				}
				weight += next.weight;
				index++;
			}
			splits[i] = index-1;
		}
		if (splits.length > 0) {
			while (true) {
				int _1st = splits[splits.length-1] + 1;
				Preconditions.checkState(_1st < M);
				long remaining = sumWeight(_1st, M) + half2;
				if (remaining - byOrder.get(_1st).weight <= desiredWeight) {
					break;
				}
				pushBack(desiredWeight, splits, splits.length-1); //push-back one slot
			}
		}
		List<T> r = new ArrayList<T>(splits.length);
		for (int i=0; i<splits.length; i++) {
			r.add(byOrder.get(splits[i]).value);
		}
		return r;
	}
	
	private long sumWeight(int fromInclusive, int toExclusive) {
		long sum = 0;
		for (int i=fromInclusive; i<toExclusive; i++) {
			sum += byOrder.get(i).weight;
		}
		return sum;
	}
	
	private void pushBack(double desiredWeight, int[] splits, int target) {
		splits[target] += 1;
		if (target > 0) { //if this not the first split, check if we should push-back iteratively
			long sw = sumWeight(splits[target-1]+2, splits[target]+1);
			if (sw > desiredWeight) {
				pushBack(desiredWeight, splits, target-1);
			}
		}
	}
	
	private void drop(Sample<T> sample) {
		int i = byOrder.indexOf(sample);
		Sample<T> r = null;
		if (i > 0) {
			r = byOrder.get(i - 1);
		}
		if (i < byOrder.size() - 1) {
			Sample<T> r2 = byOrder.get(i + 1);
			if (r == null || r.weight > r2.weight) {
				r = r2;
			}
		}
		byOrder.remove(sample);
		byWeight.remove(sample);
		adjustWeight(r, sample.weight);
	}
	
	private void adjustWeight(Sample<T> sample, final int n) {
		Preconditions.checkArgument(n != 0);
		Preconditions.checkState(Ordering.from(COMPARATOR_WEIGHTED).isOrdered(byWeight));
		int index = byWeight.indexOf(sample);
		Preconditions.checkState(index >= 0);
		sample.weight += n;
		if (n > 0) {
			if (index > 0 && byWeight.get(index - 1).weight < sample.weight) {
				byWeight.remove(sample);
				index--;
				while (index > 0 && byWeight.get(index - 1).weight < sample.weight) {
					index--;
				}
				byWeight.add(index, sample);
			}
		} else {
			byWeight.remove(sample);
			while (index < byWeight.size() && byWeight.get(index).weight > sample.weight) {
				index++;
			}
			byWeight.add(index, sample);
		}
		Preconditions.checkState(Ordering.from(COMPARATOR_WEIGHTED).isOrdered(byWeight));
	}
	
	private boolean needBalance() {
		if (byWeight.size() > 1) {
			double t = 1.0 * sumWeight(0, byWeight.size()) / byWeight.size();
			return byWeight.get(0).weight > Math.ceil(t * 2);
		}
		return false;
	}
	
	private void balance() {
		final int STEP = 3;
		for (int i=0; needBalance() && i < STEP; i++) {
			final Sample<T> heavy = byWeight.get(0);
			final int index = byOrder.indexOf(heavy);
			final Sample<T> prev = index > 0 ? byOrder.get(index - 1) : null;
			final T mid = dividable.divide(prev != null ? prev.value : null, heavy.value);
			final int H = heavy.weight/2;
			final Sample<T> partial = new Sample<T>(mid, 0);
			adjustWeight(heavy, -H);
			byOrder.add(index, partial);
			byWeight.add(partial);
			adjustWeight(partial, H);
			while (byOrder.size() > maxSamples) { //merge
				drop(byWeight.get(byWeight.size() - 1));
			}
		}
	}
	
	static private class Sample<T> {
		private T value;
		private int weight;
		public Sample(T sample, int count) {
			this.value = sample;
			this.weight = count;
		}
	}
	
	static public class SampleUnderrunException extends RuntimeException {
		public SampleUnderrunException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 2013912351878015898L;
	}

    /*static public final Dividable<byte[]> BYTE_DIVIDABLE = new Dividable<byte[]>() {
        @Override
        public byte[] divide(byte[] a, byte[] b) {
            if (a == null) {
                a = new byte[]{};
            }
            byte[][] r = Bytes.split(a, b, 1);
            return r[1];
        }

        @Override
        public int compare(byte[] o1, byte[] o2) {
            return Bytes.BYTES_COMPARATOR.compare(a, b);
        }
    };*/
}
