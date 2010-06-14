/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.util.i18n;

import com.aoindustries.sql.Database;

/**
 * Stores resources in a JDBC database.  The tables should have the following
 * structure:
 * &lt;pre&gt;
 * create table resource_bundles (
 *   id text primary key check (id=trim(id)),
 *   comments text not null
 * );
 * insert into locale_variants values(...);
 *
 * create table locale_languages (
 *   id text primary key check (id=lower(trim(id)))
 * );
 * insert into locale_languages values(...);
 *
 * create table locale_countries (
 *   id text primary key check (id=upper(trim(id)))
 * );
 * insert into locale_countries values(...);
 *
 * create table locale_variants (
 *   id text primary key check (id=trim(id))
 * );
 * insert into locale_variants values(...);
 *
 * create table resource_bundle_values (
 *   id serial primary key,
 *   resource_bundle text not null references resource_bundles(id),
 *   language text not null references locale_languages(id),
 *   country text not null references locale_countries(id),
 *   variant text not null references locale_variants(id),
 *   key text not null,
 *   last_modified timestamp not null,
 *   last_verified timestamp not null,
 *   value text not null,
 *   check (last_verified>=last_modified), -- Modifying implies verified
 *   unique (resource_bundle, key, language, country, variant)
 * );
 * &lt;/pre&gt;
 *
 * TODO: We are sticking with property-backed resource bundles, finish this class when first needed.
 *
 * @author  AO Industries, Inc.
 */
abstract public class DatabaseResourceBundle extends ModifiableResourceBundle {

    private final Database database;
    private final String baseName;
    private final String tableName;
    private final String idColumnName;

    protected DatabaseResourceBundle(Database database, String baseName) {
        this(database, baseName, "resource_bundle_values", "id");
    }

    public DatabaseResourceBundle(Database database, String baseName, String tableName, String idColumnName) {
        this.database = database;
        this.baseName = baseName;
        this.tableName = tableName;
        this.idColumnName = idColumnName;
    }
}
