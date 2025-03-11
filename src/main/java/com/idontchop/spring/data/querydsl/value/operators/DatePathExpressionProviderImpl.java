package com.idontchop.spring.data.querydsl.value.operators;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DatePath;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

class DatePathExpressionProviderImpl extends BaseExpressionProvider<DatePath> {

    public DatePathExpressionProviderImpl() {
        super(List.of(Operator.EQUAL, Operator.NOT_EQUAL, Operator.GREATER_THAN, Operator.GREATER_THAN_OR_EQUAL,
                Operator.LESS_THAN, Operator.LESS_THAN_OR_EQUAL));
    }

    @Override
    protected String getStringValue(DatePath path, Object value) {
        return value.toString();
    }

    @Override
    protected BooleanExpression eq(DatePath path, String value, boolean ignoreCase) {
        Validate.isTrue(isDate(value), "Invalid date value");
        return path.eq(convertToDate(value));
    }

    @Override
    protected BooleanExpression ne(DatePath path, String value, boolean ignoreCase) {
        Validate.isTrue(isDate(value), "Invalid date value");
        return path.ne(convertToDate(value));
    }

    @Override
    protected BooleanExpression contains(DatePath path, String value, boolean ignoreCase) {
        throw new UnsupportedOperationException("Datetime can't be searched using contains operator");
    }

    @Override
    protected BooleanExpression startsWith(DatePath path, String value, boolean ignoreCase) {
        throw new UnsupportedOperationException("Datetime can't be searched using startsWith operator");
    }

    @Override
    protected BooleanExpression endsWith(DatePath path, String value, boolean ignoreCase) {
        throw new UnsupportedOperationException("Datetime can't be searched using endsWith operator");
    }

    @Override
    protected BooleanExpression matches(DatePath path, String value) {
        throw new UnsupportedOperationException("Datetime can't be searched using matches operator");
    }

    @Override
    protected BooleanExpression gt(DatePath path, String value) {
        Validate.isTrue(isDate(value), "Invalid date value");
        return path.gt(convertToDate(value));
    }

    @Override
    protected BooleanExpression gte(DatePath path, String value) {
        Validate.isTrue(isDate(value), "Invalid date value");
        return path.goe(convertToDate(value));
    }

    @Override
    protected BooleanExpression lt(DatePath path, String value) {
        Validate.isTrue(isDate(value), "Invalid date value");
        return path.lt(convertToDate(value));
    }

    @Override
    protected BooleanExpression lte(DatePath path, String value) {
        Validate.isTrue(isDate(value), "Invalid date value");
        return path.loe(convertToDate(value));
    }

    private boolean isDate(String dateString) {
        try {
            LocalDate.parse(dateString);
            return true;
        } catch (DateTimeParseException iae) {
            return false;
        }
    }

    private LocalDate convertToDate(String dateString) {
        // use the same conversion as used by the Conversion service
        return LocalDate.parse(dateString);
    }
}
