/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate methods for a more formal definition and analysis of their
 * best, worst, and average case complexities.
 *
 * @author  AO Industries, Inc.
 */
@Documented
@Retention(RetentionPolicy.CLASS) // Allow static analysis tools
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Complexity {
    GrowthFunction best();
    GrowthCondition[] bestConditions() default {};
    GrowthFunction average();
    GrowthCondition[] averageConditions() default {};
    GrowthFunction worst();
    GrowthCondition[] worstConditions() default {};
}