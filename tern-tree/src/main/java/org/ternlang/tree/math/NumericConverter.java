package org.ternlang.tree.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.ternlang.core.convert.ConstraintAdapter;
import org.ternlang.core.variable.Value;
import org.ternlang.core.variable.ValueCache;

public enum NumericConverter {
   BIG_DECIMAL(ValueCalculator.BIG_DECIMAL, 0){
      @Override
      public Value convert(Number number) {
         BigDecimal value = adapter.createBigDecimal(number);
         return ValueCache.getBigDecimal(value);
      }
   },
   BIG_INTEGER(ValueCalculator.BIG_INTEGER, 1){
      @Override
      public Value convert(Number number) {
         BigInteger value = adapter.createBigInteger(number);
         return ValueCache.getBigInteger(value);
      }
   },
   DOUBLE(ValueCalculator.DOUBLE, 2){
      @Override
      public Value convert(Number number) {
         Double value = adapter.createDouble(number);
         return ValueCache.getDouble(value);
      }
   },
   LONG(ValueCalculator.LONG, 3){
      @Override
      public Value convert(Number number) {
         Long value = adapter.createLong(number);
         return ValueCache.getLong(value);
      }
   },
   FLOAT(ValueCalculator.FLOAT, 4){
      @Override
      public Value convert(Number number) {
         Float value = adapter.createFloat(number);
         return ValueCache.getFloat(value);
      }
   },
   INTEGER(ValueCalculator.INTEGER, 5){
      @Override
      public Value convert(Number number) {
         Integer value = adapter.createInteger(number);
         return ValueCache.getInteger(value);
      }
   },
   CHARACTER(ValueCalculator.INTEGER, 6){
      @Override
      public Value convert(Number number) {
         Integer value = adapter.createInteger(number);
         return ValueCache.getInteger(value);
      }
   },
   SHORT(ValueCalculator.SHORT, 7){
      @Override
      public Value convert(Number number) {
         Short value = adapter.createShort(number);
         return ValueCache.getShort(value);
      }
   },
   BYTE(ValueCalculator.BYTE, 8){
      @Override
      public Value convert(Number number) {
         Byte value = adapter.createByte(number);
         return ValueCache.getByte(value);
      }
   };

   public final ValueCalculator calculator;
   public final ConstraintAdapter adapter;
   public final int index;

   private NumericConverter(ValueCalculator calculator, int index) {
      this.adapter = new ConstraintAdapter();
      this.calculator = calculator;
      this.index = index;
   }

   public Value increment(Number number) {
      return calculator.add(number, 1);
   }

   public Value decrement(Number number) {
      return calculator.subtract(number, 1);
   }

   public abstract Value convert(Number number);

   public static NumericConverter resolveConverter(Value value) {
      Class type = value.getType();

      if(type != null) {
         return resolveConverter(type);
      }
      return DOUBLE;
   }

   public static NumericConverter resolveConverter(Number number) {
      Class type = number.getClass();

      if(type != null) {
         return resolveConverter(type);
      }
      return DOUBLE;
   }

   public static NumericConverter resolveConverter(Class type) {
      if (Integer.class == type) {
         return INTEGER;
      }
      if (Double.class == type) {
         return DOUBLE;
      }
      if (Long.class == type) {
         return LONG;
      }
      if (Float.class == type) {
         return FLOAT;
      }
      if (Character.class == type) {
         return CHARACTER;
      }
      if (BigDecimal.class == type) {
         return BIG_DECIMAL;
      }
      if (BigInteger.class == type) {
         return BIG_INTEGER;
      }
      if (Short.class == type) {
         return SHORT;
      }
      if (Byte.class == type) {
         return BYTE;
      }
      return DOUBLE;
   }

   public static NumericConverter resolveConverter(Value left, Value right) {
      NumericConverter primary = resolveConverter(left);
      NumericConverter secondary = resolveConverter(right);

      if(primary.index < TYPES.length && secondary.index < TYPES.length) {
         return TABLE[TYPES.length * primary.index + secondary.index];
      }
      return DOUBLE;
   }

   private static final NumericConverter[] TYPES = {
      BIG_DECIMAL,
      BIG_INTEGER,
      DOUBLE,
      LONG,
      FLOAT,
      INTEGER,
      CHARACTER,
      SHORT,
      BYTE
   };

   private static final NumericConverter[] TABLE = {
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_DECIMAL,
      BIG_INTEGER,
      BIG_DECIMAL,
      BIG_INTEGER,
      BIG_DECIMAL,
      BIG_INTEGER,
      BIG_INTEGER,
      BIG_INTEGER,
      BIG_INTEGER,
      BIG_DECIMAL,
      BIG_DECIMAL,
      DOUBLE,
      DOUBLE,
      DOUBLE,
      DOUBLE,
      DOUBLE,
      DOUBLE,
      DOUBLE,
      BIG_DECIMAL,
      BIG_INTEGER,
      DOUBLE,
      LONG,
      DOUBLE,
      LONG,
      LONG,
      LONG,
      LONG,
      BIG_DECIMAL,
      BIG_DECIMAL,
      DOUBLE,
      DOUBLE,
      FLOAT,
      FLOAT,
      FLOAT,
      FLOAT,
      FLOAT,
      BIG_DECIMAL,
      BIG_INTEGER,
      DOUBLE,
      LONG,
      FLOAT,
      INTEGER,
      INTEGER,
      INTEGER,
      INTEGER,
      BIG_DECIMAL,
      BIG_INTEGER,
      DOUBLE,
      LONG,
      FLOAT,
      INTEGER,
      INTEGER,
      INTEGER,
      INTEGER,
      BIG_DECIMAL,
      BIG_INTEGER,
      DOUBLE,
      LONG,
      FLOAT,
      INTEGER,
      INTEGER,
      SHORT,
      SHORT,
      BIG_DECIMAL,
      BIG_INTEGER,
      DOUBLE,
      LONG,
      FLOAT,
      INTEGER,
      INTEGER,
      SHORT,
      BYTE
   };
}