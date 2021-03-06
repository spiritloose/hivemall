/*
 * Hivemall: Hive scalable Machine Learning Library
 *
 * Copyright (C) 2013
 *   National Institute of Advanced Industrial Science and Technology (AIST)
 *   Registration Number: H25PRO-1520
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package hivemall.common;

import hivemall.common.WeightValue.WeightValueWithCovar;
import hivemall.utils.collections.IMapIterator;
import hivemall.utils.hadoop.HiveUtils;
import hivemall.utils.lang.Copyable;
import hivemall.utils.lang.HalfFloat;
import hivemall.utils.math.MathUtils;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SpaceEfficientDenseModel implements PredictionModel {
    private static final Log logger = LogFactory.getLog(SpaceEfficientDenseModel.class);

    private int size;
    private short[] weights;
    private short[] covars;

    public SpaceEfficientDenseModel(int ndims) {
        this(ndims, false);
    }

    public SpaceEfficientDenseModel(int ndims, boolean withCovar) {
        int size = ndims + 1;
        this.size = size;
        this.weights = new short[size];
        if(withCovar) {
            short[] covars = new short[size];
            Arrays.fill(covars, HalfFloat.ONE);
            this.covars = covars;
        } else {
            this.covars = null;
        }
    }

    private float getWeight(final int i) {
        final short w = weights[i];
        return (w == HalfFloat.ZERO) ? HalfFloat.ZERO : HalfFloat.halfFloatToFloat(w);
    }

    private float getCovar(final int i) {
        return HalfFloat.halfFloatToFloat(covars[i]);
    }

    private void setWeight(final int i, final float v) {
        if(Math.abs(v) >= HalfFloat.MAX_FLOAT) {
            throw new IllegalArgumentException("Acceptable maximum weight is "
                    + HalfFloat.MAX_FLOAT + ": " + v);
        }
        weights[i] = HalfFloat.floatToHalfFloat(v);
    }

    private void setCovar(final int i, final float v) {
        if(Math.abs(v) >= HalfFloat.MAX_FLOAT) {
            throw new IllegalArgumentException("Acceptable maximum weight is "
                    + HalfFloat.MAX_FLOAT + ": " + v);
        }
        covars[i] = HalfFloat.floatToHalfFloat(v);
    }

    private void ensureCapacity(final int index) {
        if(index >= size) {
            int bits = MathUtils.bitsRequired(index);
            int newSize = (1 << bits) + 1;
            int oldSize = size;
            logger.info("Expands internal array size from " + oldSize + " to " + newSize + " ("
                    + bits + " bits)");
            this.size = newSize;
            this.weights = Arrays.copyOf(weights, newSize);
            if(covars != null) {
                this.covars = Arrays.copyOf(covars, newSize);
                Arrays.fill(covars, oldSize, newSize, HalfFloat.ONE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends WeightValue> T get(Object feature) {
        int i = HiveUtils.parseInt(feature);
        if(i >= size) {
            return null;
        }
        if(covars == null) {
            return (T) new WeightValue(getWeight(i));
        } else {
            return (T) new WeightValueWithCovar(getWeight(i), getCovar(i));
        }
    }

    @Override
    public <T extends WeightValue> void set(Object feature, T value) {
        int i = HiveUtils.parseInt(feature);
        ensureCapacity(i);
        float weight = value.get();
        setWeight(i, weight);
        if(value.hasCovariance()) {
            float covar = value.getCovariance();
            setCovar(i, covar);
        }
    }

    @Override
    public float getWeight(Object feature) {
        int i = HiveUtils.parseInt(feature);
        if(i >= size) {
            return 0f;
        }
        return getWeight(i);
    }

    @Override
    public float getCovariance(Object feature) {
        int i = HiveUtils.parseInt(feature);
        if(i >= size) {
            return 1f;
        }
        return getCovar(i);
    }

    @Override
    public void setValue(Object feature, float weight) {
        int i = HiveUtils.parseInt(feature);
        ensureCapacity(i);
        setWeight(i, weight);
    }

    @Override
    public void setValue(Object feature, float weight, float covar) {
        int i = HiveUtils.parseInt(feature);
        ensureCapacity(i);
        setWeight(i, weight);
        setCovar(i, covar);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object feature) {
        int i = HiveUtils.parseInt(feature);
        if(i >= size) {
            return false;
        }
        float w = getWeight(i);
        return w != 0.f;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V extends WeightValue> IMapIterator<K, V> entries() {
        return (IMapIterator<K, V>) new Itr();
    }

    private final class Itr implements IMapIterator<Number, WeightValue> {

        private int cursor;
        private final WeightValueWithCovar tmpWeight;

        private Itr() {
            this.cursor = -1;
            this.tmpWeight = new WeightValueWithCovar();
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public int next() {
            ++cursor;
            if(!hasNext()) {
                return -1;
            }
            return cursor;
        }

        @Override
        public Integer getKey() {
            return cursor;
        }

        @Override
        public WeightValue getValue() {
            if(covars == null) {
                float w = getWeight(cursor);
                WeightValue v = new WeightValue(w);
                v.setTouched(w != 0f);
                return v;
            } else {
                float w = getWeight(cursor);
                float cov = getCovar(cursor);
                WeightValueWithCovar v = new WeightValueWithCovar(w, cov);
                v.setTouched(w != 0.f || cov != 1.f);
                return v;
            }
        }

        @Override
        public <T extends Copyable<WeightValue>> void getValue(T probe) {
            float w = getWeight(cursor);
            tmpWeight.value = w;
            float cov = 1.f;
            if(covars != null) {
                cov = getCovar(cursor);
                tmpWeight.covariance = cov;
            }
            tmpWeight.setTouched(w != 0.f || cov != 1.f);
            probe.copyFrom(tmpWeight);
        }

    }

}
