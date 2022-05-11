/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2019, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.util;

import java.util.Objects;

/**
 * Three objects combined into a single.  Useful for returning three values combined.
 * This tuple is not comparable.
 *
 * @author  AO Industries, Inc.
 */
public class Tuple3<E1, E2, E3> {

  private final E1 element1;
  private final E2 element2;
  private final E3 element3;

  public Tuple3(
      E1 element1,
      E2 element2,
      E3 element3
  ) {
    this.element1 = element1;
    this.element2 = element2;
    this.element3 = element3;
  }

  @Override
  public String toString() {
    return
        "("
            + element1
            + ',' + element2
            + ',' + element3
            + ')';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Tuple3<?, ?, ?>)) {
      return false;
    }
    Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) obj;
    return
        Objects.equals(element1, other.element1)
            && Objects.equals(element2, other.element2)
            && Objects.equals(element3, other.element3);
  }

  private int hash;

  @Override
  public int hashCode() {
    int h = this.hash;
    if (h == 0) {
      h = Objects.hashCode(element1);
      h = h * 31 + Objects.hashCode(element2);
      h = h * 31 + Objects.hashCode(element3);
      this.hash = h;
    }
    return h;
  }

  public E1 getElement1() {
    return element1;
  }

  public E2 getElement2() {
    return element2;
  }

  public E3 getElement3() {
    return element3;
  }
}
