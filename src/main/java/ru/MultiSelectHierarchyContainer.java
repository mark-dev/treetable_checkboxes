package ru;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.Resource;
import com.vaadin.ui.CheckBox;

import java.util.*;
import java.util.function.Consumer;


/**
 * Created by mark on 31.05.2016.
 */
public class MultiSelectHierarchyContainer extends HierarchicalContainer {

    public static final String CHECKBOX_COLUMN = "checkbox_column";
    public static final String SELECTION_STATUS = "selection_status";

    private Set<Item> selectedItems = new HashSet<>();
    private Collection<Consumer<SelectionChangedEvent>> selectionChangedEventListeners = new LinkedList<>();

    public MultiSelectHierarchyContainer() {
        addContainerProperty(CHECKBOX_COLUMN, SilentCheckBox.class, null);
        addContainerProperty(SELECTION_STATUS, SelectionStatus.class, SelectionStatus.UNCHECKED);
    }


    public void addSelectionChangedEventListener(Consumer<SelectionChangedEvent> listener) {
        selectionChangedEventListeners.add(listener);
    }

    public void removeSelectionChangedEventListener(Consumer<SelectionChangedEvent> listener) {
        selectionChangedEventListeners.remove(listener);
    }

    public Set<Item> getSelectedItems() {
        return selectedItems;
    }


    public Item addCheckBoxColumn(Object itemId, String caption) {
        return addCheckBoxColumn(itemId, caption, null);
    }

    @SuppressWarnings({"unchecked"})
    public Item addCheckBoxColumn(Object itemId, String caption, Resource icon) {
        Item i = getItem(itemId);
        MultiSelectHierarchyContainer.SilentCheckBox cb = new MultiSelectHierarchyContainer.SilentCheckBox(caption, false);
        cb.addValueChangeListener((Property.ValueChangeListener) event -> {
            toggleSelection(itemId);
        });
        cb.setIcon(icon);
        i.getItemProperty(CHECKBOX_COLUMN).setValue(cb);
        return i;
    }

    protected void toggleSelection(Object itemId) {
        Item item = getItem(itemId);
        SelectionStatus checked = getSelectionValue(item);
        SelectionStatus newValue = checked.opposite();

        changeSelectionValue(item, newValue);
        propogateDown(itemId, newValue);
        propogateUp(itemId, newValue);
        selectionChangedEventListeners.forEach((c) -> {
            c.accept(new SelectionChangedEvent(getSelectedItems()));
        });
    }

    //Propogate indererminate or deselect to all parents
    private void propogateUp(Object itemId, SelectionStatus newValue) {
        Object parentItemId;
        Object lastCheckedItemId = itemId;
        //While has more parents
        while ((parentItemId = getParent(lastCheckedItemId)) != null) {
            Item parentItem = getItem(parentItemId);
            lastCheckedItemId = parentItemId;
            //Propogate value up to root, using indeterminate instead checked
            if (newValue == SelectionStatus.CHECKED) {
                changeSelectionValue(parentItem, SelectionStatus.INDETERMINATE);
            }
            //If we deselect entity, we need to check can we deselect parent or no?
            else {

                Collection<?> childrenIds = getChildren(parentItemId);

                Optional<?> firstCheckedChild = childrenIds.stream().filter(childId -> {
                    if (!childId.equals(itemId)) {
                        Item childItem = getItem(childId);
                        SelectionStatus childItemSelectionValue = getSelectionValue(childItem);
                        return childItemSelectionValue == SelectionStatus.CHECKED || childItemSelectionValue == SelectionStatus.INDETERMINATE;
                    }
                    //Do not track target entity itself
                    return false;
                }).findFirst();

                if (firstCheckedChild.isPresent()) {
                    //  changeSelectionValue(parentItem, INDETERMINATE);
                } else {
                    //Deselect parent due no more child selected
                    changeSelectionValue(parentItem, SelectionStatus.UNCHECKED);
                }
            }
        }
    }

    //Set same value to all childs entities recursive
    private void propogateDown(Object itemId, SelectionStatus newValue) {
        Collection<?> childrenIds = getChildren(itemId);
        if (childrenIds != null) {
            childrenIds.forEach((childId) -> {
                changeSelectionValue(getItem(childId), newValue);
                Collection<?> subChilds = getChildren(childId);
                if (subChilds != null && !subChilds.isEmpty()) {
                    propogateDown(childId, newValue);
                }
            });
        }
    }


    @SuppressWarnings({"unchecked"})
    private void changeSelectionValue(Item item, SelectionStatus newStatus) {
        //Also setup related checkbox value(silently - without value change events)

        item.getItemProperty(SELECTION_STATUS).setValue(newStatus);
        SilentCheckBox checkBox = (SilentCheckBox) item.getItemProperty(CHECKBOX_COLUMN).getValue();

        switch (newStatus) {
            case CHECKED: {
                checkBox.setInternalValue(true);
                selectedItems.add(item);
                break;
            }
            case UNCHECKED: {
                checkBox.setInternalValue(false);
                selectedItems.remove(item);
                break;
            }
            case INDETERMINATE: {
                checkBox.setInternalValue(true);
                selectedItems.remove(item);
                break;
            }
        }

    }

    private SelectionStatus getSelectionValue(Item item) {
        return (SelectionStatus) item.getItemProperty(SELECTION_STATUS).getValue();
    }

    static enum SelectionStatus {
        CHECKED, UNCHECKED, INDETERMINATE;

        public SelectionStatus opposite() {
            return (this == CHECKED || this == INDETERMINATE) ? UNCHECKED : CHECKED;
        }
    }

    public static class SilentCheckBox extends CheckBox {
        public SilentCheckBox(String caption, boolean initialState) {
            super(caption, initialState);
        }

        @Override
        public void setInternalValue(Boolean newValue) {
            super.setInternalValue(newValue);
            markAsDirty();
        }
    }


    public static class SelectionChangedEvent {
        private Collection<Item> selection;

        public SelectionChangedEvent(Collection<Item> selection) {
            this.selection = selection;
        }

        public Collection<Item> getSelection() {
            return selection;
        }
    }
}
