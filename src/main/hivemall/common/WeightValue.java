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

import hivemall.utils.lang.Copyable;

public class WeightValue implements Copyable<WeightValue> {

    protected float value;

    /** Is touched in training */
    protected boolean touched;

    public WeightValue() {}

    public WeightValue(float weight) {
        this(weight, true);
    }

    public WeightValue(float weight, boolean touched) {
        this.value = weight;
        this.touched = touched;
    }

    public float get() {
        return value;
    }

    public void set(float weight) {
        this.value = weight;
    }

    public boolean hasCovariance() {
        return false;
    }

    public float getCovariance() {
        throw new UnsupportedOperationException();
    }

    public void setCovariance(float cov) {
        throw new UnsupportedOperationException();
    }

    /** 
     * @return whether touched in training or not
     */
    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    @Override
    public void copyTo(WeightValue another) {
        another.value = this.value;
        another.touched = this.touched;
    }

    @Override
    public void copyFrom(WeightValue another) {
        this.value = another.value;
        this.touched = another.touched;
    }

    @Override
    public String toString() {
        return "WeightValue [value=" + value + "]";
    }

    public static final class WeightValueWithCovar extends WeightValue {
        public static final float DEFAULT_COVAR = 1.f;

        float covariance;

        public WeightValueWithCovar() {
            super();
        }

        public WeightValueWithCovar(float weight, float covariance) {
            this(weight, covariance, true);
        }

        public WeightValueWithCovar(float weight, float covariance, boolean touched) {
            super(weight, touched);
            this.covariance = covariance;
        }

        @Override
        public boolean hasCovariance() {
            return true;
        }

        @Override
        public float getCovariance() {
            return covariance;
        }

        @Override
        public void setCovariance(float cov) {
            this.covariance = cov;
        }

        @Override
        public void copyTo(WeightValue another) {
            super.copyTo(another);
            ((WeightValueWithCovar) another).covariance = this.covariance;
        }

        @Override
        public void copyFrom(WeightValue another) {
            super.copyFrom(another);
            this.covariance = ((WeightValueWithCovar) another).covariance;
        }

        @Override
        public String toString() {
            return "WeightValueWithCovar [value=" + value + ", covariance=" + covariance + "]";
        }
    }

}
