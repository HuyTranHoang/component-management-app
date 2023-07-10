package vn.aptech.componentmanagementapp.util;

import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

public class FormattedDoubleTableCell<S> extends TableCell<S, Double> {
    private StringConverter<Double> converter;

    public FormattedDoubleTableCell() {
        this.converter = new StringConverter<>() {
            @Override
            public String toString(Double object) {
                if (object == null) {
                    return null;
                } else {
                    return String.format("₫%,.0f", object);
                }
            }

            @Override
            public Double fromString(String string) {
                // Not used in this example, as we're only displaying the value
                return null;
            }
        };
    }

    @Override
    protected void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(converter.toString(item));
        }
    }
}
