/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2025  AO Industries, Inc.
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

package com.aoapps.hodgepodge.swing;

import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.validation.InvalidResult;
import com.aoapps.lang.validation.ValidResult;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Extends {@link JTextField} with the following features.
 *
 * <ol>
 * <li>Automatic selection when gains focus.</li>
 * <li>Parsing to the given type.</li>
 * <li>Registrable value validators once parsing completes.</li>
 * <li>Re-formatting once parsing successful and all validators have passed.</li>
 * </ol>
 *
 * <p>TODO: Should we extend {@link javax.swing.JFormattedTextField} instead?
 * Are we duplicating too much of what it already provides?</p>
 *
 * @param <T> The type for parsing, validation, and re-formatting.
 *
 * @author  AO Industries, Inc.
 */
public class AoTextField<T> extends JTextField {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, AoTextField.class);

  /**
   * Parses the non-null text into the type-specific value or {@link Optional#empty()} if field is empty
   * or effectively empty (such as white-space only depending on parser).
   *
   * @see #getValue()
   */
  // Note: This javadoc is copied to parse(String) and getValue()
  @FunctionalInterface
  public static interface Parser<T> extends Serializable {
    /**
     * Parses the non-null text into the type-specific value or {@link Optional#empty()} if field is empty
     * or effectively empty (such as white-space only depending on parser).
     */
    // Note: This javadoc is copied from Parser
    Optional<T> parse(java.lang.String text) throws ParseException;

    /**
     * Trims and parses an {@link java.lang.Integer}, returning {@link Optional#empty()} when empty after trimming.
     */
    public static final Parser<java.lang.Integer> INTEGER = (java.lang.String text) -> {
      text = Strings.trim(text);
      if (text.isEmpty()) {
        return Optional.empty();
      }
      try {
        return Optional.of(java.lang.Integer.valueOf(text));
      } catch (NumberFormatException e) {
        ParseException e2 = new ParseException(text, 0);
        e2.initCause(e);
        throw e2;
      }
    };

    /**
     * Parses a {@link java.lang.String}, returning {@link Optional#empty()} empty.
     *
     * @see String
     */
    public static final Parser<java.lang.String> STRING = (java.lang.String text) -> {
      if (text.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(text);
      }
    };

    /**
     * Trims a {@link java.lang.String}, returning {@link Optional#empty()} when empty after trimming.
     *
     * @see TrimmedString
     */
    public static final Parser<java.lang.String> TRIMMED_STRING = (java.lang.String text) -> {
      text = Strings.trim(text);
      if (text.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(text);
      }
    };
  }

  /**
   * Validates a non-null value.
   */
  // Note: This javadoc is copied to validate(T)
  public static interface Validator<T> extends Serializable {
    /**
     * Validates a non-null value.
     */
    // Note: This javadoc is copied from Validator
    ValidationResult validate(T value);

    /**
     * Validates that an {@link java.lang.Integer} is a non-zero, positive value.
     */
    public static Validator<java.lang.Integer> POSITIVE_INTEGER = (java.lang.Integer value) -> {
      if (value <= 0) {
        return new InvalidResult(RESOURCES, "notPositiveInteger", value);
      } else {
        return ValidResult.getInstance();
      }
    };
  }

  /**
   * Formats the type-specific value to text, only called for values that pass all registered validators.
   *
   * @see  #setValue(java.lang.Object)
   * @see  #removeValue()
   */
  // Note: This javadoc is copied to format(T)
  @FunctionalInterface
  public static interface Formatter<T> extends Serializable {
    /**
     * Formats the type-specific value to text, only called for values that pass all registered validators.
     *
     * @param value The new value, which may be {@code null}.
     *
     * @return  the textual representation, never null
     *
     * @see  #setValue(java.lang.Object)
     * @see  #removeValue()
     */
    // Note: This javadoc is copied from Formatter
    java.lang.String format(T value);

    /**
     * Formats by calling {@link Objects#toString(java.lang.Object, java.lang.String)} with a {@code ""} null-default.
     */
    public static final Formatter<Object> OBJECTS_TOSTRING = (Object value) -> Objects.toString(value, "");

    /**
     * Formats a trimmed {@link java.lang.String}, converting null to empty string.
     *
     * @see TrimmedString
     */
    public static final Formatter<java.lang.String> TRIMMED_STRING =
        (java.lang.String value) -> (value == null) ? "" : Strings.trim(value);
  }

  /**
   * The foreground color used when invalid.
   */
  private static final Color INVALID_FOREGROUND_COLOR = Color.RED;

  private static final long serialVersionUID = 1L;

  private final Parser<? extends T> parser;

  // Leaving type as ArrayList to avoid warning about not be serializable in serializable class
  private final ArrayList<Validator<? super T>> validators = new ArrayList<>();

  private final Formatter<? super T> formatter;

  public AoTextField(
      Parser<? extends T> parser,
      Formatter<? super T> formatter
  ) {
    super();
    this.parser = parser;
    this.formatter = formatter;
    init();
  }

  public AoTextField(
      Parser<? extends T> parser,
      Formatter<? super T> formatter,
      T value
  ) {
    super(formatter.format(value));
    this.parser = parser;
    this.formatter = formatter;
    init();
  }

  public AoTextField(
      Parser<? extends T> parser,
      Formatter<? super T> formatter,
      int columns
  ) {
    super(columns);
    this.parser = parser;
    this.formatter = formatter;
    init();
  }

  public AoTextField(
      Parser<? extends T> parser,
      Formatter<? super T> formatter,
      T value, int columns
  ) {
    super(formatter.format(value), columns);
    this.parser = parser;
    this.formatter = formatter;
    init();
  }

  public AoTextField(
      Parser<? extends T> parser,
      Formatter<? super T> formatter,
      Document doc, T value, int columns
  ) {
    super(doc, formatter.format(value), columns);
    this.parser = parser;
    this.formatter = formatter;
    init();
  }

  private void init() {
    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        // Do in later event so other FocusListener may modify content before text is selected.
        SwingUtilities.invokeLater(() -> {
          if (isFocusOwner() && getSelectedText() == null) {
            selectAll();
          }
        });
      }

      @Override
      public void focusLost(FocusEvent e) {
        try {
          setValueKnownValid(getValue().orElse(null));
        } catch (ParseException | ValidationException ex) {
          // DocumentListener sets the error foreground color
        }
      }
    });

    getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          SwingUtilities.invokeLater(AoTextField.this::revalidateValue);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          SwingUtilities.invokeLater(AoTextField.this::revalidateValue);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          SwingUtilities.invokeLater(AoTextField.this::revalidateValue);
        }
    });
  }

  private Color noErrorColor;

  /**
   * Revalidates the current field contents versus the parser and all registered validators.
   * Updates the text color to either default for parseable and valid or to red for invalid.
   *
   * <p>In the case of validators that are effected by external state, such as the value of other fields,
   * this may be called when the other field is updated to ensure consistent validation state.</p>
   */
  public void revalidateValue() {
    try {
      Optional<T> value = getValue();
      if (!isFocusOwner()) {
        setValueKnownValid(value.orElse(null));
      }
      if (noErrorColor != null) {
        setForeground(noErrorColor);
      }
    } catch (ParseException | ValidationException e) {
      if (noErrorColor == null) {
        noErrorColor = getForeground();
      }
      setForeground(INVALID_FOREGROUND_COLOR);
    }
  }

  /**
   * Adds a {@link Validator}.
   *
   * @see  #getValue()
   * @see  #removeValidator(com.aoapps.hodgepodge.swing.AoTextField.Validator)
   */
  public void addValidator(Validator<? super T> validator) {
    validators.add(validator);
  }

  /**
   * Removes a {@link Validator}, matching by object identity
   * (not by {@linkplain Object#equals(java.lang.Object) equals}).
   *
   * @see  #getValue()
   * @see  #addValidator(com.aoapps.hodgepodge.swing.AoTextField.Validator)
   */
  public void removeValidator(Validator<? super T> validator) {
    // Remove by identity
    validators.removeIf(v -> v == validator);
  }

  /**
   * @deprecated  Should not be used directly, use {@link #getValue()} instead.
   */
  @Override
  @Deprecated(forRemoval = false)
  public java.lang.String getText() {
    return super.getText();
  }

  /**
   * @deprecated  Should not be used directly, use {@link #getValue()} instead.
   */
  @Override
  @Deprecated(forRemoval = false)
  public java.lang.String getText(int offs, int len) throws BadLocationException {
    return super.getText(offs, len);
  }

  /**
   * Parses the text into the type-specific value or {@link Optional#empty()} if field is empty
   * or effectively empty (such as white-space only depending on parser).
   *
   * <p>If has a value, validates against each registered {@link Validator}.</p>
   *
   * @return  The valid value, if present.
   *
   * @see  #parser
   * @see  #validators
   * @see  Parser
   * @see  Validator
   */
  // Note: This javadoc is copied from Parser
  public Optional<T> getValue() throws ParseException, ValidationException {
    Optional<? extends T> value = parser.parse(getText());
    if (value.isPresent()) {
      T v = value.get();
      for (Validator<? super T> validator : validators) {
        ValidationResult result = validator.validate(v);
        if (!result.isValid()) {
          throw new ValidationException(result);
        }
      }
      return Optional.of(v);
    } else {
      return Optional.empty();
    }
  }

  /**
   * @deprecated  Should not be used directly, use {@link #setValue(java.lang.Object)} or
   *              {@link #removeValue()} instead.
   */
  @Override
  @Deprecated(forRemoval = false)
  public void setText(java.lang.String t) {
    super.setText(t);
  }

  /**
   * Formats and sets the textual value of the field.
   *
   * <p>Only calls {@link #setText(java.lang.String)} when value changed to minimize firing of events.</p>
   *
   * @param value The new value that has already passed all registered validators, which may be {@code null}.
   *
   * @see  Formatter
   * @see  #setValue(java.lang.Object)
   * @see  #removeValue()
   */
  private void setValueKnownValid(T value) {
    java.lang.String newText = formatter.format(value);
    if (!newText.equals(getText())) {
      setText(newText);
    }
  }

  /**
   * Formats and sets the textual value of the field.
   *
   * <p>If value is non-null, validates against each registered {@link Validator}.</p>
   *
   * <p>Only calls {@link #setText(java.lang.String)} when value changed to minimize firing of events.</p>
   *
   * <p>When setting to a {@code null} value, may use {@link #removeValue()} that does not throw
   * {@link ValidationException}.</p>
   *
   * @param value The new value, which may be {@code null}.
   *
   * @see  Formatter
   * @see  #setValueKnownValid(java.lang.Object)
   * @see  #removeValue()
   */
  public void setValue(T value) throws ValidationException {
    if (value != null) {
      for (Validator<? super T> validator : validators) {
        ValidationResult result = validator.validate(value);
        if (!result.isValid()) {
          throw new ValidationException(result);
        }
      }
    }
    setValueKnownValid(value);
  }

  /**
   * Removes the value of the field.
   *
   * <p>Only calls {@link #setText(java.lang.String)} when value changed to minimize firing of events.</p>
   *
   * @see  #setValueKnownValid(java.lang.Object)
   * @see  #setValue(java.lang.Object)
   */
  public void removeValue() {
    setValueKnownValid(null);
  }

  /**
   * A text field containing an {@link java.lang.Integer} value.
   *
   * @see  Parser#INTEGER
   * @see  Formatter#OBJECTS_TOSTRING
   */
  public static class Integer extends AoTextField<java.lang.Integer> {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_COLUMNS = 11;

    static {
      assert DEFAULT_COLUMNS == java.lang.Integer.toString(java.lang.Integer.MIN_VALUE).length();
      assert DEFAULT_COLUMNS >= java.lang.Integer.toString(java.lang.Integer.MAX_VALUE).length();
    }

    /**
     * Defaults to columns able to hold the value of {@link java.lang.Integer#MIN_VALUE}.
     */
    public Integer() {
      this(DEFAULT_COLUMNS);
    }

    /**
     * Defaults to columns able to hold the value of {@link java.lang.Integer#MIN_VALUE}.
     */
    public Integer(java.lang.Integer value) {
      this(value, DEFAULT_COLUMNS);
    }

    public Integer(int columns) {
      super(Parser.INTEGER, Formatter.OBJECTS_TOSTRING, columns);
    }

    public Integer(java.lang.Integer value, int columns) {
      super(Parser.INTEGER, Formatter.OBJECTS_TOSTRING, value, columns);
    }

    public Integer(Document doc, java.lang.Integer value, int columns) {
      super(Parser.INTEGER, Formatter.OBJECTS_TOSTRING, doc, value, columns);
    }
  }

  /**
   * A text field containing a {@link java.lang.String} value without any trimming.
   * The value is never an empty string.
   * Setting the value to an empty string is equivalent to setting it to {@code null}.
   *
   * @see  Parser#STRING
   * @see  Formatter#OBJECTS_TOSTRING
   */
  public static class String extends AoTextField<java.lang.String> {

    private static final long serialVersionUID = 1L;

    public String() {
      super(Parser.STRING, Formatter.OBJECTS_TOSTRING);
    }

    public String(java.lang.String value) {
      super(Parser.STRING, Formatter.OBJECTS_TOSTRING, value);
    }

    public String(int columns) {
      super(Parser.STRING, Formatter.OBJECTS_TOSTRING, columns);
    }

    public String(java.lang.String value, int columns) {
      super(Parser.STRING, Formatter.OBJECTS_TOSTRING, value, columns);
    }

    public String(Document doc, java.lang.String value, int columns) {
      super(Parser.STRING, Formatter.OBJECTS_TOSTRING, doc, value, columns);
    }

    /**
     * Does not throw {@link ParseException}.
     */
    @Override
    public Optional<java.lang.String> getValue() throws ValidationException {
      try {
        return super.getValue();
      } catch (ParseException e) {
        throw new AssertionError("Does not throw ParseException", e);
      }
    }
  }

  /**
   * A text field containing a trimmed {@link java.lang.String}.
   * The value is never an empty string.
   * Setting the value to an empty string (after trimming) is equivalent to setting it to {@code null}.
   *
   * @see  Parser#TRIMMED_STRING
   * @see  Formatter#TRIMMED_STRING
   */
  public static class TrimmedString extends AoTextField<java.lang.String> {

    private static final long serialVersionUID = 1L;

    public TrimmedString() {
      super(Parser.TRIMMED_STRING, Formatter.TRIMMED_STRING);
    }

    public TrimmedString(java.lang.String value) {
      super(Parser.TRIMMED_STRING, Formatter.TRIMMED_STRING, value);
    }

    public TrimmedString(int columns) {
      super(Parser.TRIMMED_STRING, Formatter.TRIMMED_STRING, columns);
    }

    public TrimmedString(java.lang.String value, int columns) {
      super(Parser.TRIMMED_STRING, Formatter.TRIMMED_STRING, value, columns);
    }

    public TrimmedString(Document doc, java.lang.String value, int columns) {
      super(Parser.TRIMMED_STRING, Formatter.TRIMMED_STRING, doc, value, columns);
    }

    /**
     * Does not throw {@link ParseException}.
     */
    @Override
    public Optional<java.lang.String> getValue() throws ValidationException {
      try {
        return super.getValue();
      } catch (ParseException e) {
        throw new AssertionError("Does not throw ParseException", e);
      }
    }
  }
}
