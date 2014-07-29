/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2014  AO Industries, Inc.
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
package com.aoindustries.sql;

import com.aoindustries.util.AoCollections;
import com.aoindustries.util.IntList;
import com.aoindustries.util.LongList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wraps and simplifies access to a JDBC database.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractDatabaseAccess implements DatabaseAccess {

	private static final ObjectFactory<BigDecimal> bigDecimalObjectFactory = new ObjectFactory<BigDecimal>() {
		@Override
		public BigDecimal createObject(ResultSet result) throws SQLException {
			return result.getBigDecimal(1);
		}
	};

	@Override
    final public BigDecimal executeBigDecimalQuery(String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, RuntimeException.class, bigDecimalObjectFactory, sql, params);
    }

	@Override
    final public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(isolationLevel, readOnly, rowRequired, RuntimeException.class, bigDecimalObjectFactory, sql, params);
	}

	@Override
    final public boolean executeBooleanQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeBooleanQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

	@Override
    abstract public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException;

	private static final ObjectFactory<byte[]> byteArrayObjectFactory = new ObjectFactory<byte[]>() {
		@Override
		public byte[] createObject(ResultSet result) throws SQLException {
			return result.getBytes(1);
		}
	};

	@Override
    final public byte[] executeByteArrayQuery(String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, RuntimeException.class, byteArrayObjectFactory, sql, params);
    }

	@Override
    final public byte[] executeByteArrayQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(isolationLevel, readOnly, rowRequired, RuntimeException.class, byteArrayObjectFactory, sql, params);
	}

	private static final ObjectFactory<Date> dateObjectFactory = new ObjectFactory<Date>() {
		@Override
		public Date createObject(ResultSet result) throws SQLException {
			return result.getDate(1);
		}
	};

	@Override
    final public Date executeDateQuery(String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, RuntimeException.class, dateObjectFactory, sql, params);
    }

	@Override
    final public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(isolationLevel, readOnly, rowRequired, RuntimeException.class, dateObjectFactory, sql, params);
	}

	@Override
    final public IntList executeIntListQuery(String sql, Object ... params) throws SQLException {
        return executeIntListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

	@Override
    abstract public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException;

	@Override
    final public int executeIntQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeIntQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

	@Override
    abstract public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException;

	@Override
    final public LongList executeLongListQuery(String sql, Object ... params) throws SQLException {
        return executeLongListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

	@Override
    abstract public LongList executeLongListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException;

	@Override
    final public long executeLongQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeLongQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

	@Override
    abstract public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException;

	private static class ClassObjectFactory<T> implements ObjectFactory<T> {

		private final Class<T> clazz;
		private final Constructor<T> constructor;

		private ClassObjectFactory(Class<T> clazz) throws SQLException {
			this.clazz = clazz;
			try {
				this.constructor = clazz.getConstructor(ResultSet.class);
            } catch(NoSuchMethodException err) {
                throw new SQLException("Unable to find constructor: "+clazz.getName()+"(java.sql.ResultSet)", err);
            }
		}

		@Override
		public T createObject(ResultSet result) throws SQLException {
			try {
				return constructor.newInstance(result);
            } catch(InstantiationException err) {
                throw new SQLException("Unable to instantiate object: "+clazz.getName()+"(java.sql.ResultSet)", err);
            } catch(IllegalAccessException err) {
                throw new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)", err);
            } catch(InvocationTargetException err) {
                throw new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)", err);
            }
		}
	};

	@Override
    final public <T> T executeObjectQuery(Class<T> clazz, String sql, Object ... params) throws NoRowException, SQLException {
        return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, RuntimeException.class, new ClassObjectFactory<T>(clazz), sql, params);
    }

	@Override
    final public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws NoRowException, SQLException {
        return executeObjectQuery(isolationLevel, readOnly, rowRequired, RuntimeException.class, new ClassObjectFactory<T>(clazz), sql, params);
	}

	@Override
    final public <T> T executeObjectQuery(ObjectFactory<T> objectFactory, String sql, Object ... params) throws NoRowException, SQLException {
        return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, RuntimeException.class, objectFactory, sql, params);
    }

	@Override
    final public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, ObjectFactory<T> objectFactory, String sql, Object ... params) throws NoRowException, SQLException {
        return executeObjectQuery(isolationLevel, readOnly, rowRequired, RuntimeException.class, objectFactory, sql, params);
	}
	
	@Override
    final public <T,E extends Exception> T executeObjectQuery(Class<E> eClass, ObjectFactoryE<T,E> objectFactory, String sql, Object ... params) throws NoRowException, SQLException, E {
        return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, eClass, objectFactory, sql, params);
	}

	@Override
    abstract public <T,E extends Exception> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<E> eClass, ObjectFactoryE<T,E> objectFactory, String sql, Object ... params) throws NoRowException, SQLException, E;

	@Override
    final public <T> List<T> executeObjectListQuery(Class<T> clazz, String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, new ArrayList<T>(), RuntimeException.class, new ClassObjectFactory<T>(clazz), sql, params)
		);
    }

	@Override
    final public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(isolationLevel, readOnly, new ArrayList<T>(), RuntimeException.class, new ClassObjectFactory<T>(clazz), sql, params)
		);
	}

	@Override
    final public <T> List<T> executeObjectListQuery(ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, new ArrayList<T>(), RuntimeException.class, objectFactory, sql, params)
		);
    }

	@Override
    final public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(isolationLevel, readOnly, new ArrayList<T>(), RuntimeException.class, objectFactory, sql, params)
		);
	}

	@Override
    final public <T,E extends Exception> List<T> executeObjectListQuery(Class<E> eClass, ObjectFactoryE<T,E> objectFactory, String sql, Object ... params) throws SQLException, E {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, new ArrayList<T>(), eClass, objectFactory, sql, params)
		);
	}

	@Override
    final public <T,E extends Exception> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<E> eClass, ObjectFactoryE<T,E> objectFactory, String sql, Object ... params) throws SQLException, E {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(isolationLevel, readOnly, new ArrayList<T>(), eClass, objectFactory, sql, params)
		);
	}

	@Override
    final public <T,C extends Collection<? super T>> C executeObjectCollectionQuery(C collection, Class<T> clazz, String sql, Object ... params) throws SQLException {
        return executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, collection, RuntimeException.class, new ClassObjectFactory<T>(clazz), sql, params);
    }

	@Override
    final public <T,C extends Collection<? super T>> C executeObjectCollectionQuery(int isolationLevel, boolean readOnly, C collection, Class<T> clazz, String sql, Object ... params) throws SQLException {
        return executeObjectCollectionQuery(isolationLevel, readOnly, collection, RuntimeException.class, new ClassObjectFactory<T>(clazz), sql, params);
	}

	@Override
    final public <T,C extends Collection<? super T>> C executeObjectCollectionQuery(C collection, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        return executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, collection, RuntimeException.class, objectFactory, sql, params);
    }

	@Override
    final public <T,C extends Collection<? super T>> C executeObjectCollectionQuery(int isolationLevel, boolean readOnly, C collection, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        return executeObjectCollectionQuery(isolationLevel, readOnly, collection, RuntimeException.class, objectFactory, sql, params);
	}

	@Override
    final public <T,C extends Collection<? super T>,E extends Exception> C executeObjectCollectionQuery(C collection, Class<E> eClass, ObjectFactoryE<T,E> objectFactory, String sql, Object ... params) throws SQLException, E {
        return executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, collection, eClass, objectFactory, sql, params);
	}

	@Override
    abstract public <T,C extends Collection<? super T>,E extends Exception> C executeObjectCollectionQuery(int isolationLevel, boolean readOnly, C collection, Class<E> eClass, ObjectFactoryE<T,E> objectFactory, String sql, Object ... params) throws SQLException, E;

	@Override
    final public void executeQuery(ResultSetHandler resultSetHandler, String sql, Object ... params) throws SQLException {
        executeQuery(Connection.TRANSACTION_READ_COMMITTED, true, resultSetHandler, sql, params);
    }

	@Override
    abstract public void executeQuery(int isolationLevel, boolean readOnly, ResultSetHandler resultSetHandler, String sql, Object ... params) throws SQLException;

	private static final ObjectFactory<Short> shortObjectFactory = new ObjectFactory<Short>() {
		@Override
		public Short createObject(ResultSet result) throws SQLException {
			return result.getShort(1);
		}
	};

	@Override
    final public List<Short> executeShortListQuery(String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
		    executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, new ArrayList<Short>(), RuntimeException.class, shortObjectFactory, sql, params)
		);
    }

	@Override
    final public List<Short> executeShortListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
		    executeObjectCollectionQuery(isolationLevel, readOnly, new ArrayList<Short>(), RuntimeException.class, shortObjectFactory, sql, params)
		);
	}

	@Override
    final public short executeShortQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeShortQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

	@Override
    abstract public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException;

	private static final ObjectFactory<String> stringObjectFactory = new ObjectFactory<String>() {
		@Override
		public String createObject(ResultSet result) throws SQLException {
			return result.getString(1);
		}
	};

	@Override
    final public String executeStringQuery(String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, RuntimeException.class, stringObjectFactory, sql, params);
    }

	@Override
    final public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(isolationLevel, readOnly, rowRequired, RuntimeException.class, stringObjectFactory, sql, params);
	}

	@Override
    final public List<String> executeStringListQuery(String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, new ArrayList<String>(), RuntimeException.class, stringObjectFactory, sql, params)
		);
    }

	@Override
    final public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
		return AoCollections.optimalUnmodifiableList(
			executeObjectCollectionQuery(isolationLevel, readOnly, new ArrayList<String>(), RuntimeException.class, stringObjectFactory, sql, params)
		);
	}

	private static final ObjectFactory<Timestamp> timestampObjectFactory = new ObjectFactory<Timestamp>() {
		@Override
		public Timestamp createObject(ResultSet result) throws SQLException {
			return result.getTimestamp(1);
		}
	};

	@Override
    final public Timestamp executeTimestampQuery(String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, RuntimeException.class, timestampObjectFactory, sql, params);
    }

	@Override
    final public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
	    return executeObjectQuery(isolationLevel, readOnly, rowRequired, RuntimeException.class, timestampObjectFactory, sql, params);
	}

	@Override
	abstract public int executeUpdate(String sql, Object ... params) throws SQLException;
}
